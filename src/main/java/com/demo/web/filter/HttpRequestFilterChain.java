package com.demo.web.filter;

import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;


public class HttpRequestFilterChain {

  private final Queue<HttpRequestFilter> filters;

  public HttpRequestFilterChain(final List<HttpRequestFilter> filters) {
    this.filters = filters.stream().sorted(Comparator.comparingInt(HttpRequestFilter::getPriority))
        .collect(Collectors.toCollection(ArrayDeque::new));
  }

  public HttpResponse doNext(HttpRequest request) {
    var next = filters.poll();
    if (next == null) {
      throw new IllegalStateException("No http request filter left");
    }
    return next.filter(request, this);
  }
}
