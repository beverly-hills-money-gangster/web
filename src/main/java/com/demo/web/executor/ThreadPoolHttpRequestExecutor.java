package com.demo.web.executor;

import com.demo.annotation.Component;
import com.demo.web.exception.ExceptionListener;
import com.demo.web.exception.ExceptionToResponseConverter;
import com.demo.web.filter.HttpRequestFilter;
import com.demo.web.model.HttpResponse;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default thread pool implementation of HTTP request executor
 */
@Component
public class ThreadPoolHttpRequestExecutor extends HttpRequestExecutor implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolHttpRequestExecutor.class);

  private final ExecutorService executor = Executors.newFixedThreadPool(
      Runtime.getRuntime().availableProcessors() * 2,
      BasicThreadFactory.builder()
          .namingPattern("worker-%d")
          .daemon(false)
          .priority(Thread.NORM_PRIORITY)
          .build());

  public ThreadPoolHttpRequestExecutor(
      ExceptionToResponseConverter exceptionToResponseConverter,
      List<HttpRequestFilter> httpRequestFilters,
      List<ExceptionListener> exceptionListeners) {
    super(exceptionToResponseConverter, httpRequestFilters, exceptionListeners);
  }


  @Override
  protected Future<HttpResponse> executeImpl(Callable<HttpResponse> callable) {
    return executor.submit(callable);
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
