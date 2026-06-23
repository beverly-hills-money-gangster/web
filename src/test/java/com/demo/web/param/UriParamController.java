package com.demo.web.param;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import lombok.Getter;

@Component
public class UriParamController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/param";

  public UriParamController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    var params = new HashMap<>();

    request.getUri().readAllURIParams().forEach(
        param -> params.put(param.getKey(), param.getValue()));

    // basic validation
    request.getUri().readAllURIParams().forEach(param -> {
      var paramValue = request.getUri().getURIParamValue(param.getKey())
          .orElseThrow(() -> new IllegalStateException(
              "Missing value for param %s".formatted(param.getKey())));
      if (!paramValue.equals(param.getValue())) {
        throw new IllegalStateException(
            "Param values mismatch. Check param: %s".formatted(param.getKey()));
      }
    });
    return httpBodyFactory.json(params, HttpResponseCode.SUCCESS);
  }

}
