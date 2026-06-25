package com.demo.web.actuator;

import com.demo.annotation.Component;
import com.demo.web.health.HealthIndicator;
import com.demo.web.health.HealthStatus;

@Component
public class FailHealthIndicator implements HealthIndicator {

  @Override
  public HealthStatus getStatus() {
    return HealthStatus.DOWN;
  }
}
