package com.demo.web.health;

/**
 * Health indicator interface. More indicators can be registered by creating @Component-annotated
 * classes that implement the interface.
 */
public interface HealthIndicator {

  HealthStatus getStatus();
}
