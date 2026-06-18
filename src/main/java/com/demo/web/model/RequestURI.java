package com.demo.web.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;


public class RequestURI {

  @Getter
  private final String uri;
  @Getter
  private final String baseURI;

  private final Map<String, String> params;

  public RequestURI(final @NonNull String uri) {
    this.uri = uri;
    params = new HashMap<>();
    var splitURI = uri.split("\\?", 2);
    baseURI = uri.split("\\?", 2)[0];

    if (splitURI.length < 2) {
      return;
    }
    var splitParams = splitURI[1].split("&");
    for (String splitParam : splitParams) {
      var splitKeyValue = splitParam.split("=", 2);
      if (splitKeyValue.length < 2) {
        continue;
      }
      params.put(splitKeyValue[0], splitKeyValue[1]);
    }
  }

  public Optional<String> getURIParamValue(final @NonNull String paramName) {
    return Optional.ofNullable(params.get(paramName));
  }


  @Override
  public String toString() {
    return uri;
  }
}
