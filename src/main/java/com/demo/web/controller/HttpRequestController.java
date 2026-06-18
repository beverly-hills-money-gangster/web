package com.demo.web.controller;

import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HttpRequestController {

  protected final HttpBodyFactory httpBodyFactory;

  public abstract String getUriPattern();

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
