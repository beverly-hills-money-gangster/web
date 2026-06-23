package com.demo.web.pattern;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

@Component
public class UriPatternController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/user/*/wallet/*";

  public UriPatternController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.text(request.getUri().getUri(), HttpResponseCode.SUCCESS);
  }

}
