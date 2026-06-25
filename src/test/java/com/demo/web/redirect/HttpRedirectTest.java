package com.demo.web.redirect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpRedirectTest extends WebTest {

  @Test
  public void testGetRedirect() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/redirect".formatted(PORT))).build());

    assertEquals(301, response.statusCode());
    assertEquals("http://example.com", response.headers().firstValue("Location").get());
  }

}
