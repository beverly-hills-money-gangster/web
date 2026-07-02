package com.demo.web.executor;

import com.demo.web.exception.ExceptionListener;
import com.demo.web.exception.ExceptionToResponseConverter;
import com.demo.web.filter.HttpRequestFilter;
import com.demo.web.filter.HttpRequestFilterChain;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.util.Constants;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

/**
 * Abstract HTTP request executor
 */
@RequiredArgsConstructor
public abstract class HttpRequestExecutor {

  private final ExceptionToResponseConverter exceptionToResponseConverter;

  private final List<HttpRequestFilter> httpRequestFilters;

  private final List<ExceptionListener> exceptionListeners;

  public final HttpResponse execute(
      final @NonNull HttpRequest request,
      final @NonNull String connectionId) {
    try {
      return executeImpl(() -> {
        try {
          MDC.put(Constants.MDC_CONNECTION_ID, connectionId);
          return new HttpRequestFilterChain(httpRequestFilters).doNext(request);
        } finally {
          MDC.remove(Constants.MDC_CONNECTION_ID);
        }
      }).get();
    } catch (Exception e) {
      exceptionListeners.forEach(listener -> listener.listen(e));
      var exceptionToConvert = e instanceof ExecutionException ? e.getCause() : e;
      return exceptionToResponseConverter.apply(exceptionToConvert);
    }
  }

  /**
   * Defines how request handling logic has to be executed.
   * For example, one request -> one thread, or one request -> virtual thread.
   */
  protected abstract Future<HttpResponse> executeImpl(Callable<HttpResponse> callable);
}
