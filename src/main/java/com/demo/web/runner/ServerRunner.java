package com.demo.web.runner;

import com.demo.annotation.Component;
import com.demo.web.config.WebConfig;
import com.demo.web.exception.ExceptionListener;
import com.demo.web.executor.HttpRequestExecutor;
import com.demo.web.executor.SocketExecutor;
import com.demo.web.reader.HttpRequestReader;
import com.demo.web.validation.PortValidator;
import com.demo.web.writer.HttpResponseWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class ServerRunner implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(ServerRunner.class);

  private final SocketExecutor socketExecutor;
  private final HttpRequestReader httpRequestReader;
  private final HttpResponseWriter httpResponseWriter;
  private final HttpRequestExecutor httpRequestExecutor;
  private final PortValidator portValidator;
  private final WebConfig webConfig;
  private final List<ExceptionListener> exceptionListeners;

  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final AtomicReference<ServerSocket> serverSocketReference = new AtomicReference<>();

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
        var clientSocket = serverSocket.accept();
        socketExecutor.execute(clientSocket, socket -> {
          var connectionId = UUID.randomUUID().toString();
          try (socket) {
            socket.setSoTimeout(webConfig.getMaxIOReadTimeMls());
            LOG.debug("Accepted {}", socket);
            var out = socket.getOutputStream();
            boolean keepReading = true;
            while (keepReading) {
              var request = httpRequestReader.read(socket.getInputStream());
              if (request == null) {
                LOG.debug("No request read {}", socket);
                return;
              }
              keepReading = request.isKeepAlive();
              httpResponseWriter.write(out, httpRequestExecutor.execute(request, connectionId));
              LOG.debug("Keep reading {}", socket);
            }
          } catch (SocketTimeoutException timeoutException) {
            LOG.debug("Read timeout for socket {}", clientSocket, timeoutException);
          } catch (Exception e) {
            exceptionListeners.forEach(listener -> listener.listen(e));
          } finally {
            LOG.debug("Close connection {}", socket);
          }
        });
      }
    } catch (SocketException e) {
      if (!closed.get()) {
        throw e;
      }
    }
    LOG.info("Server {} stop", port);
  }

  @Override
  public void close() {
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
