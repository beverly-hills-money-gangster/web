package com.demo.web.filter;

import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Http request filter chain implementation. Each filter either returns a response or passes it to
 * the next filter.
 */
public class HttpRequestFilterChain {

  private final Queue<HttpRequestFilter> filters;

  public HttpRequestFilterChain(final List<HttpRequestFilter> filters) {
    this.filters = filters.stream()
        // sort based on priority
        .sorted(Comparator.comparingInt(HttpRequestFilter::getPriority))
        .collect(Collectors.toCollection(ArrayDeque::new));
  }

  /**
   * Takes next filter in the chain and executes its logic. If we are in the head of the chain, then
   * it picks up the first filter in the chain. After that, either a response is returned or
   * execution is passed to the next filter in the chain
   */
  public HttpResponse doNext(HttpRequest request) {
    var next = filters.poll();
    if (next == null) {
      throw new IllegalStateException("No http request filter left");
    }
    return next.filter(request, this);
  }
}
