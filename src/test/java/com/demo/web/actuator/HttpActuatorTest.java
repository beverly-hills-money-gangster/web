package com.demo.web.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import com.demo.web.health.HealthStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HttpActuatorTest extends WebTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testGetAllHealth() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/actuator/health".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(500, response.statusCode());
    var health = objectMapper.readValue(response.body(),
        new TypeReference<HashMap<String, HealthStatus>>() {
        });
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
    var expectedHealth = Map.of(
        "PingHealthIndicator", HealthStatus.UP,
        "FailHealthIndicator", HealthStatus.DOWN);
    assertEquals(expectedHealth, health);
  }

  @Test
  public void testGetHealthListAll() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(
                "http://127.0.0.1:%s/actuator/health?indicator=PingHealthIndicator,FailHealthIndicator"
                    .formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(500, response.statusCode());
    var health = objectMapper.readValue(response.body(),
        new TypeReference<HashMap<String, HealthStatus>>() {
        });
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
    var expectedHealth = Map.of(
        "PingHealthIndicator", HealthStatus.UP,
        "FailHealthIndicator", HealthStatus.DOWN);
    assertEquals(expectedHealth, health);
  }

  @Test
  public void testGetHealthPing() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(
                "http://127.0.0.1:%s/actuator/health?indicator=PingHealthIndicator"
                    .formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(200, response.statusCode());
    var health = objectMapper.readValue(response.body(),
        new TypeReference<HashMap<String, HealthStatus>>() {
        });
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
    var expectedHealth = Map.of(
        "PingHealthIndicator", HealthStatus.UP);
    assertEquals(expectedHealth, health);
  }

  @Test
  public void testGetHealthFailure() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(
                "http://127.0.0.1:%s/actuator/health?indicator=FailHealthIndicator"
                    .formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(500, response.statusCode());
    var health = objectMapper.readValue(response.body(),
        new TypeReference<HashMap<String, HealthStatus>>() {
        });
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
    var expectedHealth = Map.of(
        "FailHealthIndicator", HealthStatus.DOWN);
    assertEquals(expectedHealth, health);
  }

  @Test
  public void testGetHealthNonExisting() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(
                "http://127.0.0.1:%s/actuator/health?indicator=NonExisting"
                    .formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(404, response.statusCode());
  }
}
