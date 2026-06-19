package com.demo.web.json;

import com.demo.annotation.Component;
import com.demo.web.controller.AbstractJsonHttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

@Component
public class JsonController extends AbstractJsonHttpRequestController {

  private final JsonSamplePojo samplePojo = JsonSamplePojo.createDummy();

  @Getter
  private final String uriPattern = "/json";

  public JsonController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }


  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.json(samplePojo, HttpResponseCode.SUCCESS);
  }

  @Override
  public HttpResponse onPut(HttpRequest request) {
    JsonSamplePojo bodyObject = httpBodyFactory.json(request, JsonSamplePojo.class).getObject();
    return httpBodyFactory.json(bodyObject, HttpResponseCode.SUCCESS);
  }

  @Override
  public HttpResponse onPost(HttpRequest request) {
    JsonSamplePojo bodyObject = httpBodyFactory.json(request, JsonSamplePojo.class).getObject();
    return httpBodyFactory.json(bodyObject, HttpResponseCode.SUCCESS);
  }

  @Override
  public HttpResponse onPatch(HttpRequest request) {
    JsonSamplePojo bodyObject = httpBodyFactory.json(request, JsonSamplePojo.class).getObject();
    return httpBodyFactory.json(bodyObject, HttpResponseCode.SUCCESS);
  }

  @Override
  public HttpResponse onDelete(HttpRequest request) {
    return httpBodyFactory.json(samplePojo, HttpResponseCode.SUCCESS);
  }


}
