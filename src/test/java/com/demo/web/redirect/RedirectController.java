package com.demo.web.redirect;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import lombok.Getter;

@Component
public class RedirectController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/redirect";

  public RedirectController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.redirect("http://example.com");
  }

}
