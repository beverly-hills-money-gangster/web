package com.demo.web.html;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import lombok.Getter;

@Component
public class HtmlController extends HttpRequestController {

  public static String HTML = """
      <!DOCTYPE html>
      <html lang="en">
      <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Hello World</title>
      </head>
      <body>
          <h1>Hello, World!</h1>
      </body>
      </html>""";

  @Getter
  private final String uriPattern = "/html";

  public HtmlController(HttpBodyFactory httpBodyFactory) {
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    return httpBodyFactory.html(HTML, HttpResponseCode.SUCCESS);
  }

}
