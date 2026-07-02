package com.demo.web.validation;

import java.util.Set;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class CookieValidator {

  private static final Set<String> INVALID_CHARS = Set.of(";", " ", "\n", "\t", "=", ",", "(", ")",
      "<", ">", "@", ":", "/", "\\", "\"", "[", "]", "?", "{", "}");

  public void validate(final @NonNull String name, final @NonNull String value) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Blank cookie");
    } else if (containsInvalidChar(name) || containsInvalidChar(value)) {
      throw new IllegalArgumentException("Invalid cookie");
    }
  }

  private boolean containsInvalidChar(final String text) {
    for (String invalidChar : INVALID_CHARS) {
      if (text.contains(invalidChar)) {
        return true;
      }
    }
    return false;
  }
}