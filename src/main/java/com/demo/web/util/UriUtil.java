package com.demo.web.util;

import org.apache.commons.io.FilenameUtils;

public class UriUtil {

  public static boolean patternMatch(String uri, String pattern) {
    return (uri.equals(pattern) // fast escape
        || countSlashes(pattern) == countSlashes(uri) && FilenameUtils.wildcardMatch(uri,
        pattern));
  }

  private static int countSlashes(String s) {
    int count = 0;
    for (int i = 0, n = s.length(); i < n; i++) {
      if (s.charAt(i) == '/') {
        count++;
      }
    }
    return count;
  }

}
