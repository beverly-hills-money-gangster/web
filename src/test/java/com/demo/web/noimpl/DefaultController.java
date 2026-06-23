package com.demo.web.noimpl;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import lombok.Getter;

// Default controller with no implementation
@Component
public class DefaultController extends HttpRequestController {

  public DefaultController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Getter
  private final String uriPattern = "/default";
}
