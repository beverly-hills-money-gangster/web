package com.demo.web.validation;

import java.util.Set;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

// TODO test it
public class CookieValidator {

  private static final Set<String> INVALID_CHARS = Set.of(";", " ", "\n", "\t", "=");

  public void validate(final @NonNull String name, final @NonNull String value) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Blank cookie");
    } else if (containsInvalidChar(name)) {
      throw new IllegalArgumentException("Invalid cookie name");
    } else if (containsInvalidChar(value)) {
      throw new IllegalArgumentException("Invalid cookie value");
    }
  }

  private boolean containsInvalidChar(String text) {
    for (String invalidChar : INVALID_CHARS) {
      if (text.contains(invalidChar)) {
        return true;

      }
    }
    return false;
  }
}