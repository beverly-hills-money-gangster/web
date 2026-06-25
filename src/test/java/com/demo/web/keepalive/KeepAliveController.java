package com.demo.web.keepalive;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.util.Constants;
import lombok.Getter;
import org.slf4j.MDC;

@Component
public class KeepAliveController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/keep-alive";

  public KeepAliveController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.text(MDC.get(Constants.MDC_CONNECTION_ID), HttpResponseCode.SUCCESS);
  }
}
