package com.demo.web.exception;

import com.demo.annotation.Component;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private final HttpBodyFactory httpBodyFactory;

  public HttpResponse handle(final Throwable error) {
    if (error instanceof HTTPProtocolException) {
      LOG.warn("Error occurred", error);
      return httpBodyFactory.text(error, HttpResponseCode.BAD_REQUEST);
    } else {
      LOG.error("Error occurred", error);
      return httpBodyFactory.text(error, HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }
}
