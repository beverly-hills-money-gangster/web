package com.demo.web.controller;

import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpContentType;

/**
 * Json HTTP controller
 */
public abstract class AbstractJsonHttpRequestController extends HttpRequestController {

  public AbstractJsonHttpRequestController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  /**
   * By default, JSON content-type is required
   */
  @Override
  public HttpContentType getRequiredContentType() {
    return HttpContentType.JSON;
  }

}
