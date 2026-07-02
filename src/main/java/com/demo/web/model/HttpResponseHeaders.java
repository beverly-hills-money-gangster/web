package com.demo.web.model;

import com.demo.web.validation.CookieValidator;
import java.util.ArrayList;
import java.util.Optional;
import lombok.NonNull;

/**
 * Response-specific HTTP header storage
 */
public class HttpResponseHeaders extends HttpHeaders {

  private static final CookieValidator COOKIE_VALIDATOR = new CookieValidator();

  /**
   * Adds a cookie to the response
   */
  public void addCookie(final @NonNull ResponseCookie cookie) {
    COOKIE_VALIDATOR.validate(cookie.getName(), cookie.getValue());
    var cookieBuilder = new ArrayList<String>();
    cookieBuilder.add("%s=%s".formatted(cookie.getName(), cookie.getValue()));
    Optional.ofNullable(cookie.getMaxAgeSeconds()).ifPresent(
        maxAge -> cookieBuilder.add("Max-Age=%s".formatted(maxAge)));
    Optional.ofNullable(cookie.getSameSite()).ifPresent(
        sameSite -> cookieBuilder.add("SameSite=%s".formatted(sameSite.getValue())));

    if (cookie.isSecure()) {
      cookieBuilder.add("Secure");
    }
    if (cookie.isHttpOnly()) {
      cookieBuilder.add("HttpOnly");
    }
    add("set-cookie", String.join("; ", cookieBuilder));
  }

}
