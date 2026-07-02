package com.demo.web.runner;

import com.demo.annotation.Component;
import com.demo.web.config.WebServerConfig;
import com.demo.web.exception.ExceptionListener;
import com.demo.web.exception.ExceptionToResponseConverter;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.executor.HttpRequestExecutor;
import com.demo.web.executor.IOExecutor;
import com.demo.web.model.HttpRequest;
import com.demo.web.reader.HttpRequestReader;
import com.demo.web.validation.PortValidator;
import com.demo.web.writer.HttpResponseWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web-server runner. Execution is split into 2 workloads: IO and request handling.
 * <p>
 * By default, IO workload(read request, write response) is taken care by virtual threads. No
 * pinning and CPU work to be executed here.
 * <p>
 * Request handling, unlike IO, is processed by a classic fixed thread pool as client code may
 * include CPU-heavy logic or pinning.
 */
@Component
@RequiredArgsConstructor
public class ServerRunner implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(ServerRunner.class);

  private final IOExecutor ioExecutor;
  private final HttpRequestReader httpRequestReader;
  private final HttpResponseWriter httpResponseWriter;
  private final HttpRequestExecutor httpRequestExecutor;
  private final PortValidator portValidator;
  private final WebServerConfig webServerConfig;
  private final List<ExceptionListener> exceptionListeners;
  private final ExceptionToResponseConverter exceptionToResponseConverter;

  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final AtomicReference<ServerSocket> serverSocketReference = new AtomicReference<>();

  /**
   * Runs a web-server. Blocks until closed.
   */
  public void start(int port) throws IOException {
    if (closed.get()) {
      throw new IllegalStateException("Can't start closed server runner");
    } else if (!started.compareAndSet(false, true)) {
      throw new IllegalStateException("Can't start server runner twice");
    }
    portValidator.validate(port);
    try (var serverSocket = new ServerSocket(port)) {
      serverSocketReference.set(serverSocket);
      LOG.info("Server start {}", serverSocket);
      while (!closed.get()) {
        // accept a socket and execute
        execute(serverSocket.accept());
      }
    } catch (SocketException e) {
      if (!closed.get()) {
        throw e;
      }
    }
    LOG.info("Server {} stop", port);
  }

  private void execute(final Socket clientSocket) {
    // make sure no pinning is happening here
    ioExecutor.execute(clientSocket, socket -> {
      var connectionId = UUID.randomUUID().toString();
      try (socket) {
        socket.setSoTimeout(webServerConfig.getMaxIOReadTimeMls());
        LOG.debug("Accepted {}", socket);
        var out = socket.getOutputStream();
        boolean keepReading = true;
        while (keepReading) {
          HttpRequest request;
          try {
            request = httpRequestReader.read(socket.getInputStream());
          } catch (HTTPProtocolException e) {
            httpResponseWriter.write(out, exceptionToResponseConverter.apply(e));
            throw e;
          }
          if (request == null) {
            LOG.debug("No request read {}", socket);
            return;
          }
          keepReading = request.isKeepAlive();
          // processes the request and send the response back to the socket
          httpResponseWriter.write(out, httpRequestExecutor.execute(request, connectionId));
          LOG.debug("Keep reading {}", socket);
        }
      } catch (Exception e) {
        exceptionListeners.forEach(listener -> listener.listen(e));
      } finally {
        LOG.debug("Close connection {}", socket);
      }
    });
  }

  @Override
  public void close() {
    if (closed.get()) {
      return;
    }
    closed.set(true);
    Optional.ofNullable(serverSocketReference.get()).ifPresent(serverSocket -> {
      try {
        serverSocket.close();
      } catch (IOException e) {
        LOG.error("Can't close server socket {}", serverSocket, e);
      }
    });
  }
}
