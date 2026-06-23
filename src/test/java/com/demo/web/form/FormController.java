package com.demo.web.form;

import com.demo.annotation.Component;
import com.demo.web.controller.AbstractFormDataHttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

@Component
public class FormController extends AbstractFormDataHttpRequestController {

  @Getter
  private final String uriPattern = "/form";

  public FormController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }


  @Override
  public HttpResponse onPost(HttpRequest request) {
    var form = httpBodyFactory.form(request).getForm();
    return httpBodyFactory.json(form, HttpResponseCode.SUCCESS);
  }

}
