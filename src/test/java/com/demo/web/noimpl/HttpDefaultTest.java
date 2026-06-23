package com.demo.web.noimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import com.demo.web.json.JsonSamplePojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpDefaultTest extends WebTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testJsonGet() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/default".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(405, response.statusCode());
  }

  @Test
  public void testJsonPost() throws JsonProcessingException {
    var jsonObjectToSend = JsonSamplePojo.createDummy();
    var jsonToSend = objectMapper.writeValueAsString(jsonObjectToSend);
    var response = sendRequest(
        HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(jsonToSend))
            .uri(URI.create("http://127.0.0.1:%s/default".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(405, response.statusCode());
  }


  @Test
  public void testJsonPut() throws JsonProcessingException {
    var jsonObjectToSend = JsonSamplePojo.createDummy();
    var jsonToSend = objectMapper.writeValueAsString(jsonObjectToSend);
    var response = sendRequest(
        HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(jsonToSend))
            .uri(URI.create("http://127.0.0.1:%s/default".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(405, response.statusCode());
  }

  @Test
  public void testJsonDelete() {
    var response = sendRequest(
        HttpRequest.newBuilder().DELETE()
            .uri(URI.create("http://127.0.0.1:%s/default".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(405, response.statusCode());
  }

}
