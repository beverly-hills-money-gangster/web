package com.demo.web.mustache;

import com.demo.annotation.Component;
import com.demo.web.controller.HttpRequestController;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import java.util.Map;
import lombok.Getter;

@Component
public class MustacheController extends HttpRequestController {

  @Getter
  private final String uriPattern = "/mustache";

  private final Mustache sampleMustache;

  public MustacheController(HttpBodyFactory httpBodyFactory) {
    var mf = new DefaultMustacheFactory();
    sampleMustache = mf.compile("sample.mustache");
    super(httpBodyFactory);
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    Map<String, Object> context = Map.of("house", Map.of(
        "Patrick", "rock",
        "SpongeBob", "pineapple",
        "Sandy", "tank")
    );
    return httpBodyFactory.html(sampleMustache, context, HttpResponseCode.SUCCESS);
  }

}
