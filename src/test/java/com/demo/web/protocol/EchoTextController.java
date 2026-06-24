package com.demo.web.protocol;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

@Component
public class EchoTextController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/echo";

  public EchoTextController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.text(HttpResponseCode.SUCCESS);
  }

  @Override
  public HttpResponse onPost(HttpRequest request) {
    return httpBodyFactory.text(request.getBody(), HttpResponseCode.SUCCESS);
  }

}
