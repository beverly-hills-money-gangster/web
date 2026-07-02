package com.demo.web.filter;

import com.demo.annotation.Component;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.registry.HttpControllerRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Main HTTP request filter. This is the bottom of the filter chain.
 */
@Component
@RequiredArgsConstructor
public class HttpRequestHandleFilter extends HttpRequestFilter {

  private final HttpControllerRegistry httpControllerRegistry;

  @Getter
  private final int priority = Integer.MAX_VALUE; // should be executed last

  @Override
  public HttpResponse filter(HttpRequest request, HttpRequestFilterChain chain) {
    var handler = httpControllerRegistry.getController(request);
    return switch (request.getMethod()) {
      case GET -> handler.onGet(request);
      case PUT -> handler.onPut(request);
      case POST -> handler.onPost(request);
      case DELETE -> handler.onDelete(request);
      case PATCH -> handler.onPatch(request);
    };
  }
}
