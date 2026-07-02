package com.demo.web.controller;

import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpContentType;

/**
 * Form data HTTP controller
 */
public abstract class AbstractFormDataHttpRequestController extends HttpRequestController {

  public AbstractFormDataHttpRequestController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  /**
   * By default, FORM content-type is required
   */
  @Override
  public HttpContentType getRequiredContentType() {
    return HttpContentType.FORM_URLENCODED;
  }

}
