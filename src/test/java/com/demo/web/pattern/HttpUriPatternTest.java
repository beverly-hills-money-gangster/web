package com.demo.web.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpUriPatternTest extends WebTest {

  @Test
  public void testGetPatternMatch() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/user/123/wallet/456".formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals("/user/123/wallet/456", response.body());
  }

  @Test
  public void testGetPatternNoMatchMissingUserId() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/user/wallet/456".formatted(PORT)))
            .build());
    assertEquals(404, response.statusCode());
  }

  @Test
  public void testGetPatternNoMatchMultipleUserIds() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/user/123/321/wallet/456".formatted(PORT)))
            .build());
    assertEquals(404, response.statusCode());
  }

  @Test
  public void testGetPatternNoMatchMissingWalletId() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/user/123/wallet".formatted(PORT)))
            .build());
    assertEquals(404, response.statusCode());
  }

}