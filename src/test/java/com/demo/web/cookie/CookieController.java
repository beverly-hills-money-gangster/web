package com.demo.web.cookie;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import java.util.HashMap;
import lombok.Getter;

@Component
public class CookieController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/cookie";

  public CookieController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    var cookies = new HashMap<String, String>();
    for (String cookie : request.getHeaders().getAllCookies()) {
      request.getHeaders().getCookie(cookie).ifPresent(value -> cookies.put(cookie, value));
    }
    return httpBodyFactory.json(cookies, HttpResponseCode.SUCCESS);
  }

}
