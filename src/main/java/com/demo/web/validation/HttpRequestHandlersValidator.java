package com.demo.web.validation;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HttpRequestHandlersValidator implements Validator<List<HttpRequestController>> {

  @Override
  public void validate(List<HttpRequestController> handlersToValidate) {
    var allURIPatterns = handlersToValidate.stream()
        .collect(Collectors.groupingBy(HttpRequestController::getUriPattern));
    allURIPatterns.forEach((uriPattern, handlers) -> {
      if (handlers.size() > 1) {
        throw new IllegalStateException("URI patterns should be unique. See: %s"
            .formatted(handlers.stream().map(handler -> handler.getClass().getCanonicalName())
                .collect(Collectors.toSet()))
        );
      }
    });
  }
}
