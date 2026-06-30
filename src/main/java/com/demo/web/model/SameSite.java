package com.demo.web.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SameSite {
  STRICT("Strict"), LAX("Lax"), NONE("None");

  @Getter
  private final String value;

}
