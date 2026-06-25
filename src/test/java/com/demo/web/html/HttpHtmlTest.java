package com.demo.web.html;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpHtmlTest extends WebTest {


  @Test
  public void testGetHtml() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/html".formatted(PORT))).build());

    assertEquals(200, response.statusCode());
    assertEquals("text/html; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
    assertEquals(HtmlController.HTML, response.body());
  }

}
