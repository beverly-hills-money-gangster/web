package com.demo.web.keepalive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

public class HttpKeepAliveTest extends WebTest {

  @Test
  public void testKeepAlive() {
    var request = HttpRequest.newBuilder().GET()
        .uri(URI.create("http://127.0.0.1:%s/keep-alive".formatted(PORT))).build();
    List<HttpRequest> requests = List.of(request, request, request, request, request);
    int totalRequests = requests.size();

    var responses = sendRequests(requests);
    assertEquals(totalRequests, responses.size());

    Set<String> connectionIds = new HashSet<>();
    for (HttpResponse<String> response : responses) {
      assertEquals(200, response.statusCode());
      assertEquals("text/plain; charset=UTF-8",
          response.headers().firstValue("Content-Type").get());
      assertTrue(StringUtils.isNotBlank(response.body()));
      connectionIds.add(response.body());
    }
    assertEquals(1, connectionIds.size(),
        "Connection id should be the same for all requests. Actual: " + connectionIds);
  }

  @Test
  public void testKeepAliveNewConnectionEveryCall() {
    var request = HttpRequest.newBuilder().GET()
        .uri(URI.create("http://127.0.0.1:%s/keep-alive".formatted(PORT))).build();
    List<HttpRequest> requests = List.of(request, request, request, request, request);
    int totalRequests = requests.size();

    var responses = requests.stream().map(this::sendRequest).toList();

    assertEquals(totalRequests, responses.size());
    Set<String> connectionIds = new HashSet<>();
    for (HttpResponse<String> response : responses) {
      assertEquals(200, response.statusCode());
      assertEquals("text/plain; charset=UTF-8",
          response.headers().firstValue("Content-Type").get());
      assertTrue(StringUtils.isNotBlank(response.body()));
      connectionIds.add(response.body());
    }
    assertEquals(totalRequests, connectionIds.size(),
        "Connection id should different the same for all requests. Actual: " + connectionIds);
  }
}
