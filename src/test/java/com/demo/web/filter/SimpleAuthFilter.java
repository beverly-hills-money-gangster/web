package com.demo.web.filter;

import com.demo.annotation.Component;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SimpleAuthFilter extends HttpRequestFilter {

  @Getter
  private final int priority = 0;

  private final HttpBodyFactory httpBodyFactory;

  @Override
  public HttpResponse filter(HttpRequest request, HttpRequestFilterChain chain) {
    if (!request.getUri().getUri().equals("/auth")) {
      return chain.doNext(request);
    }
    var password = request.getHeaders().getOne("password").orElse("");
    if (password.equals("123456")) {
      return chain.doNext(request);
    } else {
      return httpBodyFactory.text(HttpResponseCode.FORBIDDEN);
    }
  }
}
