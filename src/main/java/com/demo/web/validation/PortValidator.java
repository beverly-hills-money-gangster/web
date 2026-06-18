package com.demo.web.validation;

import com.demo.annotation.Component;

@Component
public class PortValidator implements Validator<Integer> {

  @Override
  public void validate(Integer port) {
    if (!(port >= 1 && port <= 65535)) {
      throw new IllegalArgumentException("Invalid port %s".formatted(port));
    }
  }
}
