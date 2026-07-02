package com.demo.web.health;

import com.demo.annotation.Component;

/**
 * Basic ping health indicator. Always UP. Can be used to test network connection with the server.
 * It's either going to respond with UP or give a network error.
 */
@Component
public class PingHealthIndicator implements HealthIndicator {

  @Override
  public HealthStatus getStatus() {
    // always UP
    return HealthStatus.UP;
  }
}
