package com.demo.web.registry;

import static com.demo.web.util.UriUtil.patternMatch;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.util.Constants;
import com.demo.web.validation.HttpRequestHandlersValidator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HttpControllerRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(HttpControllerRegistry.class);

  private final List<HttpRequestController> httpRequestControllers;

  public HttpControllerRegistry(List<HttpRequestController> httpRequestControllers,
      HttpRequestHandlersValidator httpRequestHandlersValidator) {
    httpRequestHandlersValidator.validate(httpRequestControllers);
    this.httpRequestControllers = httpRequestControllers;
  }

  public HttpRequestController getHandler(final HttpRequest request) {
    var requestContentType = request.getHeaders().getOne(Constants.CONTENT_TYPE_HEADER)
        .map(contentType -> HttpContentType.get(contentType).orElseThrow(
            () -> new HTTPProtocolException("Not support type %s".formatted(contentType),
                HttpResponseCode.UNSUPPORTED_MEDIA_TYPE))).orElse(null);

    var candidates = httpRequestControllers.stream()
        .filter(httpRequestHandler -> patternMatch(
            request.getUri().getBaseURI(), httpRequestHandler.getUriPattern()))
        .toList();

    if (candidates.isEmpty()) {
      throw new HTTPProtocolException(HttpResponseCode.NOT_FOUND);
    } else if (candidates.size() > 1) {
      LOG.error("Ambiguous HTTP controller situation. Expected 1 controller. Check {}",
          candidates.stream()
              .map(httpRequestHandler -> httpRequestHandler.getClass().getCanonicalName())
              .collect(Collectors.toSet()));
      throw new HTTPProtocolException(HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
    return Optional.of(candidates.getFirst()).filter(
        handler -> handler.getRequiredContentType() == null
            || requestContentType == handler.getRequiredContentType()).orElseThrow(
        () -> new HTTPProtocolException(HttpResponseCode.UNSUPPORTED_MEDIA_TYPE));
  }
}
