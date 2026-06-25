package com.demo.web.util;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class TextUtil {

  public static Set<String> splitCommaSeparated(final @NonNull String text) {
    if (StringUtils.isBlank(text)) {
      return Set.of();
    }
    Set<String> response = new HashSet<>();
    for (String item : text.split(",")) {
      response.add(item.trim());
    }
    return response;
  }

}
