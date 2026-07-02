package com.demo.web.exception;

import com.demo.annotation.Component;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

/**
 * Turns unhandled exceptions into HTTP response objects
 */
@Component
@RequiredArgsConstructor
public class ExceptionToResponseConverter implements Function<Throwable, HttpResponse> {

  private final HttpBodyFactory httpBodyFactory;

  @Override
  public HttpResponse apply(final Throwable error) {
    if (error instanceof HTTPProtocolException httpError) {
      return httpBodyFactory.text(httpError, httpError.getHttpResponseCode());
    } else {
      return httpBodyFactory.text(error, HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }
}
