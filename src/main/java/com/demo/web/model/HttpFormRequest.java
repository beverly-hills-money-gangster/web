package com.demo.web.model;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;


@RequiredArgsConstructor
public class HttpFormRequest {

  @Delegate
  private final HttpRequest request;

  @Getter
  private final Map<String, String> form;

}
