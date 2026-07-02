package com.demo.web.controller;

import com.demo.annotation.Component;
import com.demo.web.factory.HttpBodyFactory;
import com.demo.web.health.HealthIndicator;
import com.demo.web.health.HealthStatus;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseCode;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Health actuator HTTP controller. Returns health for all registered health indicators
 */
@Component
public class HealthActuatorController extends HttpRequestController {

  private static final Logger LOG = LoggerFactory.getLogger(HealthActuatorController.class);

  @Getter
  private final String uriPattern = "/actuator/health";

  private final List<HealthIndicator> healthIndicators;

  public HealthActuatorController(HttpBodyFactory httpBodyFactory,
      List<HealthIndicator> healthIndicators) {
    super(httpBodyFactory);
    this.healthIndicators = healthIndicators;
  }

  @Override
  public HttpResponse onGet(HttpRequest request) {
    var healthStatus = new HashMap<String, HealthStatus>();
    var targetIndicators = request.getUri().getURIParamValues("indicator").orElse(null);
    healthIndicators.stream().filter(healthIndicator
            -> targetIndicators == null
            || targetIndicators.contains(healthIndicator.getClass().getSimpleName()))
        .forEach(healthIndicator -> {
          var indicatorName = healthIndicator.getClass().getSimpleName();
          try {
            healthStatus.put(indicatorName, healthIndicator.getStatus());
          } catch (Exception e) {
            LOG.error("Health {} indicator error", indicatorName, e);
            healthStatus.put(indicatorName, HealthStatus.DOWN);
          }
        });
    boolean allHealthy = healthStatus.values().stream().allMatch(HealthStatus.UP::equals);
    if (healthStatus.isEmpty()) {
      return httpBodyFactory.text(HttpResponseCode.NOT_FOUND);
    } else {
      return httpBodyFactory.json(healthStatus,
          allHealthy ? HttpResponseCode.SUCCESS : HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }

}
