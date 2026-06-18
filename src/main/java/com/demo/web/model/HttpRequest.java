package com.demo.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@Builder
public class HttpRequest {

  private final RequestURI uri;
  private final HttpMethod method;
  private final HttpHeaders headers;
  private final String body;
  private final boolean keepAlive;

  @Override
  public String toString() {
    return "HttpRequest{" +
        "uri='" + uri + '\'' +
        ", method=" + method +
        ", headers=" + headers +
        '}';
  }
}
