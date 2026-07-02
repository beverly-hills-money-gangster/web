package com.demo.web.filter;

import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;

import java.util.Optional;

/**
 * HTTP request filter.
 * Each filter either has to return a response to pass it to the next filter in the chain
 * All filters are executed in a specific order based on priority(low number - high priority)
 * If we have 3 filters A(priority=0), B(priority=10), C(priority=-1), then it's going to be
 * executed in the following order: C, A, B
 */
public abstract class HttpRequestFilter {

  // low number - high priority
  protected abstract int getPriority();

  public abstract HttpResponse filter(HttpRequest request, HttpRequestFilterChain chain);

}
