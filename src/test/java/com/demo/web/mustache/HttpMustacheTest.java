package com.demo.web.mustache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpMustacheTest extends WebTest {

  @Test
  public void testGetHtml() {
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create("http://127.0.0.1:%s/mustache".formatted(PORT))).build());

    assertEquals(200, response.statusCode());
    assertEquals("text/html; charset=UTF-8",
        response.headers().firstValue("Content-Type").get());
    assertEqualsHttpFriendly("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>Where Everyone Lives</title>
        </head>
        <body>
            <h1>Where Everyone Lives</h1>
        
            <ul>
                <li>Patrick lives under a rock.</li>
                <li>SpongeBob lives in a pineapple.</li>
                <li>Sandy lives in a tank.</li>
            </ul>
        
        </body>
        </html>""", response.body());
  }
}
