package com.demo.web.factory;

import com.demo.annotation.Component;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpFormRequest;
import com.demo.web.model.HttpJsonRequest;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class HttpRequestFactory {

  private final ObjectMapperFactory objectMapperFactory;

  public <T> HttpJsonRequest<T> json(final HttpRequest request, Class<T> clazz)
      throws HTTPProtocolException {
    try {
      T object = objectMapperFactory.create()
          .readValue(request.getBody(), clazz);
      return new HttpJsonRequest<T>(request, object);
    } catch (JsonProcessingException e) {
      throw new HTTPProtocolException("Can't read json", e, HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }

  public HttpFormRequest form(final HttpRequest request) {
    var formKeyValuePairs = request.getBody().split("&");
    var formData = new HashMap<String, String>();
    for (String formKeyValuePair : formKeyValuePairs) {
      var keyValue = formKeyValuePair.split("=", 2);
      if (keyValue.length < 2) {
        throw new HTTPProtocolException("Invalid form data %s".formatted(formKeyValuePair),
            HttpResponseCode.BAD_REQUEST);
      }
      formData.put(keyValue[0], keyValue[1]);
    }
    return new HttpFormRequest(request, formData);
  }
}
