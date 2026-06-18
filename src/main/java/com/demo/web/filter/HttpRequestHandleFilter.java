package com.demo.web.filter;

import com.demo.annotation.Component;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.registry.HttpControllerRegistry;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HttpRequestHandleFilter extends HttpRequestFilter {

  private final HttpControllerRegistry httpControllerRegistry;

  private final HttpBodyFactory httpBodyFactory;

  @Getter
  private final int priority = Integer.MAX_VALUE; // should be executed last

  @Override
  public Optional<HttpResponse> filter(HttpRequest request, HttpRequestFilterChain chain) {
    return httpControllerRegistry.getHandler(request)
        .map(handler ->
            switch (request.getMethod()) {
              case GET -> handler.onGet(request);
              case PUT -> handler.onPut(request);
              case POST -> handler.onPost(request);
              case DELETE -> handler.onDelete(request);
              case PATCH -> handler.onPatch(request);
            }

        ).or(() -> Optional.of(httpBodyFactory.text(HttpResponseCode.NOT_FOUND)));
  }
}
