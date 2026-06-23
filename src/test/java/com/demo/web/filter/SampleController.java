package com.demo.web.filter;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

@Component
public class SampleController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/auth";

  public SampleController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.text("Authorized", HttpResponseCode.SUCCESS);
  }

}
