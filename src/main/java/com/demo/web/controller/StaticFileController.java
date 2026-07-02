package com.demo.web.controller;

import com.demo.annotation.Component;
import com.demo.annotation.Profile;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

/**
 * Default HTTP controller that is responsible for handling static files from /resource/static
 * folder. Enabled only if "enableStaticFiles" profile is on.
 */
@Component
@Profile(profiles = "enableStaticFiles")
public class StaticFileController extends HttpRequestController {

  private static final String URI_PREFIX = "/resources/static/";

  @Getter
  private final String uriPattern = URI_PREFIX + "*";

  public StaticFileController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }


  @Override
  public HttpResponse onGet(HttpRequest request) {
    var fileName = request.getUri().getBaseURI().replace(URI_PREFIX, "");
    return httpBodyFactory.resource(fileName, HttpResponseCode.SUCCESS);
  }
}
