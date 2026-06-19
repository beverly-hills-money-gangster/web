package com.demo.web.controller;

import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpContentType;
public abstract class AbstractFormDataHttpRequestController extends HttpRequestController {

  public AbstractFormDataHttpRequestController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpContentType getRequiredContentType() {
    return HttpContentType.FORM_URLENCODED;
  }

}
