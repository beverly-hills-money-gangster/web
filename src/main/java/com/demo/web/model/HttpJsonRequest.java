package com.demo.web.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;


/**
 * Content-specific HTTP request that holds JSON data as a POJO
 */
@RequiredArgsConstructor
public class HttpJsonRequest<T> {

  @Delegate
  private final HttpRequest request;

  @Getter
  private final T object;

}
