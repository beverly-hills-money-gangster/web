package com.demo.web.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpJsonTest extends WebTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testJsonGet() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().GET().uri(URI.create("http://127.0.0.1:%s/json".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(200, response.statusCode());
    var pojo = objectMapper.readValue(response.body(), JsonSamplePojo.class);
    assertEquals(JsonSamplePojo.createDummy(), pojo);
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

  @Test
  public void testJsonPost() throws JsonProcessingException {
    var jsonObjectToSend = JsonSamplePojo.createDummy();
    var jsonToSend = objectMapper.writeValueAsString(jsonObjectToSend);
    var response = sendRequest(
        HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(jsonToSend))
            .uri(URI.create("http://127.0.0.1:%s/json".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(200, response.statusCode());
    var pojo = objectMapper.readValue(response.body(), JsonSamplePojo.class);
    assertEquals(jsonObjectToSend, pojo);
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

  @Test
  public void testJsonPut() throws JsonProcessingException {
    var jsonObjectToSend = JsonSamplePojo.createDummy();
    var jsonToSend = objectMapper.writeValueAsString(jsonObjectToSend);
    var response = sendRequest(
        HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(jsonToSend))
            .uri(URI.create("http://127.0.0.1:%s/json".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(200, response.statusCode());
    var pojo = objectMapper.readValue(response.body(), JsonSamplePojo.class);
    assertEquals(jsonObjectToSend, pojo);
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

  @Test
  public void testJsonDelete() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().DELETE()
            .uri(URI.create("http://127.0.0.1:%s/json".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(200, response.statusCode());
    var pojo = objectMapper.readValue(response.body(), JsonSamplePojo.class);
    assertEquals(JsonSamplePojo.createDummy(), pojo);
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

}
