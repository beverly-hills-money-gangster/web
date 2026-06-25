package com.demo.web.runner;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.demo.web.WebContainer;
import com.demo.web.json.JsonController;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRunnerTest {

  private static final Logger LOG = LoggerFactory.getLogger(ServerRunnerTest.class);

  @Test
  public void testStartTwice() throws InterruptedException {
    var errorsCount = new AtomicInteger();
    Thread serverThread;
    try (var container = WebContainer.init(JsonController.class, Set.of("enableStaticFiles"))) {
      var serverRunner = container.getInstance(ServerRunner.class);
      serverThread = new Thread(() -> {
        try {
          serverRunner.start(5555);
        } catch (Exception e) {
          LOG.error("Can't start server", e);
          errorsCount.incrementAndGet();
        }
      });
      serverThread.start();
      Thread.sleep(500); // wait for server to start
      var ex = assertThrows(IllegalStateException.class, () -> serverRunner.start(6666));
      assertEquals("Can't start server runner twice", ex.getMessage());
    }
    serverThread.join();
    assertEquals(0, errorsCount.get());

  }

  @Test
  public void testStartClosed() throws InterruptedException {
    var errorsCount = new AtomicInteger();
    Thread serverThread;
    try (var container = WebContainer.init(JsonController.class, Set.of("enableStaticFiles"))) {
      var serverRunner = container.getInstance(ServerRunner.class);
      serverThread = new Thread(() -> {
        try {
          serverRunner.start(5555);
        } catch (Exception e) {
          LOG.error("Can't start server", e);
          errorsCount.incrementAndGet();
        }
      });
      serverThread.start();
      Thread.sleep(500); // wait for server to start
      serverRunner.close();
      Thread.sleep(500); // wait for server to close
      var ex = assertThrows(IllegalStateException.class, () -> serverRunner.start(6666));
      assertEquals("Can't start closed server runner", ex.getMessage());
    }
    serverThread.join();
    assertEquals(0, errorsCount.get());

  }
}