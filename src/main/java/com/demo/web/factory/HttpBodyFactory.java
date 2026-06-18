package com.demo.web.factory;

import com.demo.annotation.Component;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Component
@RequiredArgsConstructor
public class HttpBodyFactory {

  @Delegate
  private final HttpResponseFactory httpResponseFactory;
  @Delegate
  private final HttpRequestFactory httpRequestFactory;

}
