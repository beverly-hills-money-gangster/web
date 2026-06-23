package com.demo.web.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class HttpFormTest extends WebTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testFormPost() throws JsonProcessingException {

    var form = new HashMap<String, String>();
    form.put("eng", "Hello World");
    form.put("rus", "Привет Мир");

    var formEncoded = form.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining("&"));

    var response = sendRequest(
        HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(formEncoded))
            .uri(URI.create("http://127.0.0.1:%s/form".formatted(PORT)))
            .header("Content-Type", "application/x-www-form-urlencoded").build());

    var echoForm = objectMapper.readValue(response.body(),
        new TypeReference<HashMap<String, String>>() {
        });
    assertEquals(form, echoForm);
    assertEquals(200, response.statusCode());
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

  @Test
  public void testFormPostMalformed() {
    var response = sendRequest(
        HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString("&&&&&===&&&&&=@#$%^&*()_"))
            .uri(URI.create("http://127.0.0.1:%s/form".formatted(PORT)))
            .header("Content-Type", "application/x-www-form-urlencoded").build());
    assertEquals("Invalid form data", response.body());
    assertEquals(400, response.statusCode());

  }

  @Test
  public void testFormPostWrongContentType() {

    var form = new HashMap<String, String>();
    form.put("eng", "Hello World");
    form.put("rus", "Привет Мир");

    var formEncoded = form.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining("&"));

    var response = sendRequest(
        HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(formEncoded))
            .uri(URI.create("http://127.0.0.1:%s/form".formatted(PORT)))
            .header("Content-Type", "application/json").build());
    assertEquals(415, response.statusCode());
    assertEquals("Unsupported Media Type", response.body());
  }

}
