package com.demo.web.error;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import lombok.Getter;

@Component
public class ErrorController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/error";


  public ErrorController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    throw new RuntimeException("Some business logic error");
  }
}
