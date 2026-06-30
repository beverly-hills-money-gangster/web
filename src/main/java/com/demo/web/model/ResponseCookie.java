package com.demo.web.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseCookie {

  private final String name;
  private final String value;
  private final Integer maxAgeSeconds;
  private final boolean secure;
  private final boolean httpOnly;
  private final SameSite sameSite;
}