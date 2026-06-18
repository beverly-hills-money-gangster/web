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
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class HealthActuatorController extends HttpRequestController {

  private static final Logger LOG = LoggerFactory.getLogger(HealthActuatorController.class);

  private static final String URI_PREFIX = "/actuator/health";

  @Getter
  private final String uriPattern = URI_PREFIX + "*";

  private final List<HealthIndicator> healthIndicators;

  public HealthActuatorController(HttpBodyFactory httpBodyFactory,
      List<HealthIndicator> healthIndicators) {
    super(httpBodyFactory);
    this.healthIndicators = healthIndicators;
  }

  public HttpResponse onGet(HttpRequest request) {
    Map<String, HealthStatus> healthStatus = new HashMap<>();
    var targetIndicator = Optional.of(request.getUri().getBaseURI()
            .replace(URI_PREFIX, "")
            .replace("/", ""))
        .filter(StringUtils::isNotBlank).orElse(null);

    healthIndicators.stream().filter(healthIndicator
            -> targetIndicator == null || targetIndicator.equals(
            healthIndicator.getClass().getSimpleName()))
        .forEach(healthIndicator -> {
          var indicatorName = healthIndicator.getClass().getSimpleName();
          try {
            healthStatus.put(indicatorName, healthIndicator.getStatus());
          } catch (Exception e) {
            LOG.error("Health {} indicator error", healthIndicator.getClass().getCanonicalName(),
                e);
            healthStatus.put(indicatorName, HealthStatus.DOWN);
          }
        });
    boolean allHealthy = healthStatus.values().stream().allMatch(HealthStatus.UP::equals);
    if (healthStatus.isEmpty()) {
      return httpBodyFactory.text(HttpResponseCode.NOT_FOUND);
    }
    return httpBodyFactory.json(healthStatus,
        allHealthy ? HttpResponseCode.SUCCESS : HttpResponseCode.INTERNAL_SERVER_ERROR);
  }

}
