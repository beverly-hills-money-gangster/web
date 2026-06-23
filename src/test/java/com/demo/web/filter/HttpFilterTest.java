package com.demo.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpFilterTest extends WebTest {


  @Test
  public void testGetAuthorizedResourceNoPassword() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/auth".formatted(PORT))).build());
    assertEquals("Forbidden", response.body());
    assertEquals(403, response.statusCode());
  }

  @Test
  public void testGetAuthorizedResourceWrongPassword() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .header("password", "wrong")
            .uri(URI.create("http://127.0.0.1:%s/auth".formatted(PORT))).build());
    assertEquals("Forbidden", response.body());
    assertEquals(403, response.statusCode());
  }

  @Test
  public void testGetAuthorizedResourceCorrectPassword() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .header("password", "123456")
            .uri(URI.create("http://127.0.0.1:%s/auth".formatted(PORT))).build());
    assertEquals("Authorized", response.body());
    assertEquals(200, response.statusCode());
  }

}