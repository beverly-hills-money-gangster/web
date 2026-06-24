package com.demo.web.executor;

import com.demo.web.exception.ExceptionResponseExecutor;
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

@RequiredArgsConstructor
public abstract class HttpRequestExecutor {

  private final ExceptionResponseExecutor exceptionResponseExecutor;

  private final List<HttpRequestFilter> httpRequestFilters;

  public final HttpResponse execute(final @NonNull HttpRequest request,
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
    } catch (ExecutionException e) {
      return exceptionResponseExecutor.execute(e.getCause());
    } catch (Exception e) {
      return exceptionResponseExecutor.execute(e);
    }
  }

  protected abstract Future<HttpResponse> executeImpl(Callable<HttpResponse> callable);
}
