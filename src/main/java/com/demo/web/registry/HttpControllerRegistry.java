package com.demo.web.registry;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpRequest;
import com.demo.web.util.Constants;
import com.demo.web.validation.HttpRequestHandlersValidator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;

@Component
public class HttpControllerRegistry {

  private final List<HttpRequestController> httpRequestControllers;

  public HttpControllerRegistry(List<HttpRequestController> httpRequestControllers,
      HttpRequestHandlersValidator httpRequestHandlersValidator) {
    httpRequestHandlersValidator.validate(httpRequestControllers);
    this.httpRequestControllers = httpRequestControllers;
  }

  public Optional<HttpRequestController> getHandler(final HttpRequest request) {
    var requestContentType = request.getHeaders().getOne(Constants.CONTENT_TYPE_HEADER)
        .map(HttpContentType::get).orElse(null);

    var candidates = httpRequestControllers.stream()
        .filter(handler
            -> handler.getRequiredContentType() == null
            || requestContentType == handler.getRequiredContentType())
        .filter(httpRequestHandler -> FilenameUtils.wildcardMatch(
            request.getUri().getBaseURI(), httpRequestHandler.getUriPattern()))
        .toList();

    if (candidates.isEmpty()) {
      return Optional.empty();
    } else if (candidates.size() > 1) {
      throw new IllegalStateException("Ambiguous HTTP handler situation. Expected 1 handler. Got %s"
          .formatted(candidates.stream()
              .map(httpRequestHandler -> httpRequestHandler.getClass().getCanonicalName())
              .collect(Collectors.toSet())));
    }
    return Optional.of(candidates.getFirst());
  }
}
