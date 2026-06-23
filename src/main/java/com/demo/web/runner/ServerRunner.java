package com.demo.web.runner;

import static com.demo.web.util.Constants.MAX_IO_READ_TIME_MLS;

import com.demo.annotation.Component;
import com.demo.web.exception.GlobalExceptionHandler;
import com.demo.web.executor.HttpRequestExecutor;
import com.demo.web.executor.SocketExecutor;
import com.demo.web.reader.HttpRequestReader;
import com.demo.web.validation.PortValidator;
import com.demo.web.writer.HttpResponseWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
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
  private final GlobalExceptionHandler globalExceptionHandler;

  private final AtomicBoolean alive = new AtomicBoolean(true);
  private final AtomicBoolean started = new AtomicBoolean(false);
  private final AtomicReference<ServerSocket> serverSocketReference = new AtomicReference<>();

  public void start(int port) throws IOException {
    if (!started.compareAndSet(false, true)) {
      // TODO test this
      // TODO make sure you can't start closed server
      throw new IllegalStateException("Can't start server runner twice");
    }
    portValidator.validate(port);
    try (var serverSocket = new ServerSocket(port)) {
      serverSocketReference.set(serverSocket);
      LOG.info("Server start {}", serverSocket);
      while (alive.get()) {
        var clientSocket = serverSocket.accept();
        socketExecutor.execute(clientSocket, socket -> {
          var connectionId = UUID.randomUUID().toString();
          try (socket) {
            socket.setSoTimeout(MAX_IO_READ_TIME_MLS);
            LOG.debug("Accepted {}", socket);
            var out = socket.getOutputStream();
            boolean keepReading = true;
            while (keepReading) {
              LOG.debug("Keep reading {}", socket);
              var request = httpRequestReader.read(socket.getInputStream());
              if (request == null) {
                LOG.debug("No request read {}", socket);
                return;
              }
              keepReading = request.isKeepAlive();
              httpResponseWriter.write(out, httpRequestExecutor.execute(request, connectionId));
            }
          } catch (SocketTimeoutException timeoutException) {
            LOG.debug("Read timeout for socket {}", clientSocket, timeoutException);
          } catch (Exception e) {
            LOG.warn("Unexpected exception", e);
          } finally {
            LOG.debug("Close connection {}", socket);
          }
        });
      }
    }
    LOG.info("Server {} stop", port);
  }

  @Override
  public void close() {
    alive.set(false);
    Optional.ofNullable(serverSocketReference.get()).ifPresent(serverSocket -> {
      try {
        serverSocket.close();
      } catch (IOException e) {
        LOG.error("Can't close server socket {}", serverSocket, e);
      }
    });
  }
}
