package com.demo.web.controller;

import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;

/**
 * Main controller class that takes care of http request handling
 */
@RequiredArgsConstructor
public abstract class HttpRequestController {

  protected final HttpBodyFactory httpBodyFactory;

  /**
   * Specified URI pattern for the controller.
   * Can be just plain string such as "/user/getAll"
   * Or a pattern such as "user/*" or "user/?". '*' - zero or more symbols, '?' - exactly one
   */
  public abstract String getUriPattern();

  /**
   * Returns required content type. By default - null (no check against content type is performed).
   */
  @Nullable
  public HttpContentType getRequiredContentType() {
    return null; // no specific content type required by default
  }

  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.text(HttpResponseCode.METHOD_NOT_ALLOWED);
  }

  public HttpResponse onPut(HttpRequest request) {
    return httpBodyFactory.text(HttpResponseCode.METHOD_NOT_ALLOWED);
  }

  public HttpResponse onPost(HttpRequest request) {
    return httpBodyFactory.text(HttpResponseCode.METHOD_NOT_ALLOWED);
  }

  public HttpResponse onPatch(HttpRequest request) {
    return httpBodyFactory.text(HttpResponseCode.METHOD_NOT_ALLOWED);
  }

  public HttpResponse onDelete(HttpRequest request) {
    return httpBodyFactory.text(HttpResponseCode.METHOD_NOT_ALLOWED);
  }

}
