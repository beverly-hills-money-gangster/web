package com.demo.web.model;

import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HttpResponse implements Closeable {

  @Nullable
  private final HttpResponseBody body;

  private final HttpResponseCode code;

  private final HttpHeaders headers;

  public HttpHeadersReader getHeaders() {
    return headers;
  }

  @Override
  public String toString() {
    return "HttpResponse{" +
        "headers=" + headers +
        ", code=" + code +
        '}';
  }

  @Override
  public void close() throws IOException {
    if (body != null) {
      body.close();
    }
  }
}
