package com.demo.web.executor;

import com.demo.annotation.Component;
import com.demo.annotation.Profile;
import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Profile(profiles = {"threadPoolExecutor"})
@Component
public class ThreadPoolSocketExecutor extends SocketExecutor implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolSocketExecutor.class);


  private final ExecutorService executor = Executors.newFixedThreadPool(
      Runtime.getRuntime().availableProcessors() * 2,
      BasicThreadFactory.builder()
          .namingPattern("server-socket-accept-%d")
          .daemon(false)
          .priority(Thread.NORM_PRIORITY)
          .build());

  @Override
  protected void executeImpl(Runnable runnable) {
    executor.execute(runnable);
  }


  @Override
  public void close() {
    executor.shutdown();
    try {
      var terminated = executor.awaitTermination(5_000, TimeUnit.MILLISECONDS);
      if (!terminated) {
        LOG.warn("Thread pool termination timeout");
      }
    } catch (InterruptedException e) {
      LOG.error("Can't close thread pool", e);
    }
  }
}
