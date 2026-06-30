package com.demo.web.model;

import com.demo.web.validation.CookieValidator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

public class HttpRequestHeaders extends HttpHeaders {

  private static final CookieValidator COOKIE_VALIDATOR = new CookieValidator();

  private final Map<String, String> cookies = new HashMap<>();

  @Override
  public void add(@NonNull final String header, @NonNull final String value) {
    super.add(header, value);
    if ("cookie".equals(header)) {
      addCookie(value);
    }
  }

  private void addCookie(String headerValue) {
    for (var cookieKeyPair : headerValue.split(";")) {
      var cookieKeyPairSplit = cookieKeyPair.split("=");
      if (cookieKeyPairSplit.length != 2) {
        throw new IllegalArgumentException("Invalid cookie");
      }
      var cookieKey = cookieKeyPairSplit[0].trim();
      var cookieValue = cookieKeyPairSplit[1].trim();
      COOKIE_VALIDATOR.validate(cookieKey, cookieValue);
      cookies.put(cookieKey, cookieValue);
    }
  }

  public Optional<String> getCookie(final String cookieName) {
    return Optional.ofNullable(cookies.get(cookieName));
  }

  public Set<String> getAllCookies() {
    return cookies.keySet();
  }

}