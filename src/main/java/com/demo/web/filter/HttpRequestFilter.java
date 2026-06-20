package com.demo.web.filter;

import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;

import java.util.Optional;

public abstract class HttpRequestFilter {

  // low number - high priority
  protected abstract int getPriority();

  public abstract HttpResponse filter(HttpRequest request, HttpRequestFilterChain chain);

}
