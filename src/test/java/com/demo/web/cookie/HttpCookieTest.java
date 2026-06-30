package com.demo.web.cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HttpCookieTest extends WebTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCookie() throws JsonProcessingException {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/cookie".formatted(PORT)))
            .header("Cookie", "spongebob=pineapple; patrick=rock") // multiple cookies in one line
            .header("cookie", "sandy=tank") // one cookie lowercased
            .build());

    var echoCookies = objectMapper.readValue(response.body(),
        new TypeReference<HashMap<String, String>>() {
        });

    assertEquals(3, echoCookies.size());
    assertEquals(Map.of("spongebob", "pineapple",
            "patrick", "rock",
            "sandy", "tank"),
        echoCookies);
    assertEquals(200, response.statusCode());
    assertEquals("application/json; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

}
