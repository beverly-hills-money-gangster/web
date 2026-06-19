package com.demo.web.file;

import static com.demo.web.model.HttpContentType.CSS;
import static com.demo.web.model.HttpContentType.CSV;
import static com.demo.web.model.HttpContentType.HTML;
import static com.demo.web.model.HttpContentType.JAVASCRIPT;
import static com.demo.web.model.HttpContentType.TEXT;
import static com.demo.web.model.HttpContentType.XML;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.web.WebTest;
import com.demo.web.util.Constants;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

public class HttpStaticFileTest extends WebTest {


  @Test
  public void testGetCss() throws IOException {
    var expectedBody = readFile("sample.css");
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(("http://127.0.0.1:%s/resources/static/sample.css").formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals(CSS.getType(), response.headers().firstValue("Content-Type").get());
    assertEquals(expectedBody, response.body());
  }

  @Test
  public void testGetCsv() throws IOException {
    var expectedBody = readFile("sample.csv");
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(("http://127.0.0.1:%s/resources/static/sample.csv").formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals(CSV.getType(), response.headers().firstValue("Content-Type").get());
    assertEquals(expectedBody, response.body());
  }


  @Test
  public void testGetHtml() throws IOException {
    var expectedBody = readFile("sample.html");
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(("http://127.0.0.1:%s/resources/static/sample.html").formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals(HTML.getType(), response.headers().firstValue("Content-Type").get());
    assertEquals(expectedBody, response.body());
  }


  @Test
  public void testGetJs() throws IOException {
    var expectedBody = readFile("sample.js");
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(("http://127.0.0.1:%s/resources/static/sample.js").formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals(JAVASCRIPT.getType(), response.headers().firstValue("Content-Type").get());
    assertEquals(expectedBody, response.body());
  }


  @Test
  public void testGetTxt() throws IOException {
    var expectedBody = readFile("sample.txt");
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(("http://127.0.0.1:%s/resources/static/sample.txt").formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals(TEXT.getType(), response.headers().firstValue("Content-Type").get());
    assertEquals(expectedBody, response.body());
  }


  @Test
  public void testGetXml() throws IOException {
    var expectedBody = readFile("sample.xml");
    var response = sendRequest(
        HttpRequest.newBuilder().GET()
            .uri(URI.create(("http://127.0.0.1:%s/resources/static/sample.xml").formatted(PORT)))
            .build());
    assertEquals(200, response.statusCode());
    assertEquals(XML.getType(), response.headers().firstValue("Content-Type").get());
    assertEquals(expectedBody, response.body());
  }

  private String readFile(String fileName) throws IOException {
    try (var in = getClass().getResourceAsStream("/static/" + fileName)) {
      if (in == null) {
        throw new IllegalStateException("File %s not found".formatted(fileName));
      }
      return new String(in.readAllBytes(), Constants.DEFAULT_CHARSET);
    }
  }

}
