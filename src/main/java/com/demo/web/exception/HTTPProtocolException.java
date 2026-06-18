package com.demo.web.exception;

public class HTTPProtocolException extends RuntimeException {

  public HTTPProtocolException(String message) {
    super(message);
  }

  public HTTPProtocolException(String message, Throwable cause) {
    super(message, cause);
  }

  public HTTPProtocolException(Throwable cause) {
    super(cause);
  }

  public HTTPProtocolException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public HTTPProtocolException() {
  }
}
