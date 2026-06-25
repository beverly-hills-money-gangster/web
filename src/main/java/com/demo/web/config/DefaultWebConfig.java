package com.demo.web.config;

import com.demo.annotation.Component;
import lombok.Getter;

@Component
public class DefaultWebConfig implements WebConfig {

  @Getter
  private final int maxIOReadTimeMls = 5_000;

  @Getter
  private final int maxBytesToRead = 1024 * 1024 * 10;

}
