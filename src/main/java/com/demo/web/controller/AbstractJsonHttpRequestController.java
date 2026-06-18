package com.demo.web.controller;

import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpContentType;

// TODO rename to Controller
public abstract class AbstractJsonHttpRequestController extends HttpRequestController {

  public AbstractJsonHttpRequestController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpContentType getRequiredContentType() {
    return HttpContentType.JSON;
  }

}
