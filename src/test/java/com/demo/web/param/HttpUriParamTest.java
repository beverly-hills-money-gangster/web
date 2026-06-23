package com.demo.web.param;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpUriParamTest extends WebTest {

  @Test
  public void testGet() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/param?abc=123&xyz=456".formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals("{\"abc\":\"123\",\"xyz\":\"456\"}", response.body());
  }

}
