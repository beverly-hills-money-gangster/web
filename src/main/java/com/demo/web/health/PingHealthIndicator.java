package com.demo.web.health;

import com.demo.annotation.Component;

@Component
public class PingHealthIndicator implements HealthIndicator {

  @Override
  public HealthStatus getStatus() {
    // always UP
    return HealthStatus.UP;
  }
}
