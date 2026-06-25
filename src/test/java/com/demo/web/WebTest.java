package com.demo.web;

import com.demo.container.Container;
import com.demo.web.bootstrap.WebContainerBootstrap;
import com.demo.web.config.DefaultWebConfig;
import com.demo.web.protocol.ExceptionCaptureListener;
import com.demo.web.runner.ServerRunner;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebTest {

  private static final Logger LOG = LoggerFactory.getLogger(WebTest.class);

  public static final int PORT = 4444;

  private static Container container;

  @BeforeAll
  public static void startServer() {
    container = WebContainer.init(
        WebContainerBootstrap.builder().source(WebTest.class)
            .decorators(Map.of(DefaultWebConfig.class, Mockito::spy))
            .profiles(Set.of("enableStaticFiles")).build());
    var serverRunner = container.getInstance(ServerRunner.class);
    new Thread(() -> {
      try {
        serverRunner.start(PORT);
      } catch (IOException e) {
        LOG.error("Server runner error", e);
      }
    }).start();
  }

  @AfterEach
  public void clearExceptionCapture() {
    container.getInstance(ExceptionCaptureListener.class).clear();
  }

  public static <T> T getComponent(Class<T> clazz) {
    return container.getInstance(clazz);
  }

  @AfterAll
  public static void stopServer() throws InterruptedException {
    Optional.ofNullable(container).ifPresent(Container::close);
    Thread.sleep(1_000); // wait a little so the port becomes available again
  }

  public HttpResponse<String> sendRequest(final HttpRequest request) {
    try (HttpClient client = HttpClient.newHttpClient()) {
      return client.send(
          request,
          HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
