package com.demo.web.registry;

import static com.demo.web.util.UriUtil.patternMatch;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.util.Constants;
import com.demo.web.validation.HttpControllerValidator;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP controller registry. Responsible for fetching a suitable request controller based on
 * content-type, url, etc.
 */
@Component
public class HttpControllerRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(HttpControllerRegistry.class);

  private final List<HttpRequestController> httpRequestControllers;

  public HttpControllerRegistry(
      final List<HttpRequestController> httpRequestControllers,
      final HttpControllerValidator httpControllerValidator) {
    httpControllerValidator.validate(httpRequestControllers);
    this.httpRequestControllers = httpRequestControllers;
  }

  public HttpRequestController getController(final @NonNull HttpRequest request) {
    var requestContentType = request.getHeaders().getOne(Constants.CONTENT_TYPE_HEADER)
        .map(contentType -> HttpContentType.get(contentType).orElseThrow(
            () -> new HTTPProtocolException("Not supported type %s".formatted(contentType),
                HttpResponseCode.UNSUPPORTED_MEDIA_TYPE))).orElse(null);

    var candidates = httpRequestControllers.stream().filter(controller ->
        patternMatch(request.getUri().getBaseURI(), controller.getUriPattern())).toList();

    if (candidates.isEmpty()) {
      throw new HTTPProtocolException(HttpResponseCode.NOT_FOUND);
    } else if (candidates.size() > 1) {
      LOG.error("Expected 1 controller but got {}", candidates.size());
      throw new HTTPProtocolException(HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
    return Optional.of(candidates.getFirst()).filter(controller ->
            controller.getRequiredContentType() == null
                || requestContentType == controller.getRequiredContentType())
        .orElseThrow(() -> new HTTPProtocolException(HttpResponseCode.UNSUPPORTED_MEDIA_TYPE));
  }
}
