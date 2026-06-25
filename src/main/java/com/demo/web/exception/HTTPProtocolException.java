package com.demo.web.exception;

import com.demo.web.model.HttpResponseCode;
import lombok.Getter;
import lombok.NonNull;

public class HTTPProtocolException extends RuntimeException {

  @NonNull
  @Getter
  private final HttpResponseCode httpResponseCode;

  public HTTPProtocolException(@NonNull HttpResponseCode httpResponseCode) {
    super(httpResponseCode.getMsg());
    this.httpResponseCode = httpResponseCode;
  }

  public HTTPProtocolException(String message, @NonNull HttpResponseCode httpResponseCode) {
    super(message);
    this.httpResponseCode = httpResponseCode;
  }

  public HTTPProtocolException(String message, Throwable cause,
      @NonNull HttpResponseCode httpResponseCode) {
    super(message, cause);
    this.httpResponseCode = httpResponseCode;
  }

}
