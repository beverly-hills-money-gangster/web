package com.demo.web.exception;

import com.demo.annotation.Component;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExceptionResponseExecutor {

  private final HttpBodyFactory httpBodyFactory;

  private final List<ExceptionListener> exceptionListeners;

  public HttpResponse execute(final Throwable error) {
    exceptionListeners.forEach(listener -> listener.listen(error));
    if (error instanceof HTTPProtocolException httpError) {
      return httpBodyFactory.text(httpError, httpError.getHttpResponseCode());
    } else {
      return httpBodyFactory.text(error, HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }
}
