package com.demo.web.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpErrorTest extends WebTest {

  @Test
  public void testErrorGet() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/error".formatted(PORT))).build());
    assertEquals(500, response.statusCode());
    assertEquals("text/plain; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
  }

}
