package com.demo.web.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.demo.web.WebTest;
import com.demo.web.config.DefaultWebConfig;
import com.demo.web.util.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ProtocolTest extends WebTest {

  private static final int MAX_BYTES_TO_READ = 1024;

  private static final int MAX_IO_READ_TIME_MLS = 1000;

  @BeforeAll
  public static void setUpConfigs() {
    var defaultConfig = getComponent(DefaultWebConfig.class);
    doReturn(MAX_BYTES_TO_READ).when(defaultConfig).getMaxBytesToRead();
    doReturn(MAX_IO_READ_TIME_MLS).when(defaultConfig).getMaxIOReadTimeMls();
  }

  @Test
  public void testGetEcho() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.1
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 200 OK
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 2
          
          OK""", response);

      assertStreamEnded(socket);
    }
  }

  @Test
  public void testMissingCookieName() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.1
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Cookie: =value
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 12
          
          Blank cookie""", response);

      assertStreamEnded(socket);
    }
  }

  @Test
  public void testMissingCookieValue() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.1
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Cookie: sessionid=
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 14
          
          Invalid cookie""", response);

      assertStreamEnded(socket);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Cookie: sess ion=123",
      "Cookie: sess?ion=123",
      "Cookie: sess{ion=123",
      "Cookie: sess}ion=123",
      "Cookie: sess;ion=123",
      "Cookie: sess,ion=123",
      "Cookie: sess\"ion=123",
      "Cookie: sess\\ion=123",
      "Cookie: sess(ion=123"})
  public void testInvalidCookieValue(final String invalidCookieHeader)
      throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.1
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          %s
          Connection: close
          
          """.formatted(invalidCookieHeader);
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 14
          
          Invalid cookie""", response);

      assertStreamEnded(socket);
    }
  }

  @Test
  public void testMalformedHeader() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.1
          Host: 127.0.0.1
          User-Agent
          Accept: application/json
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 19
          
          Invalid HTTP header""", response);

      assertStreamEnded(socket);
    }
  }


  @Test
  public void testMissingStartLine() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 23
          
          Invalid HTTP start-line""", response);
      assertStreamEnded(socket);
    }

  }


  @Test
  public void testMissingContentLength() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: application/json
          
          {
            "username": "alice",
            "password": "secret123"
          }""";
      socket.getOutputStream()
          .write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 32
          
          content-length header is missing""", response);
      assertStreamEnded(socket);
    }
  }

  @Test
  public void testContentLengthNegative() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: application/json
          Content-Length: -1
          
          {
            "username": "alice",
            "password": "secret123"
          }""";
      socket.getOutputStream()
          .write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 23
          
          Negative content-length""", response);
      assertStreamEnded(socket);
    }

  }

  @Test
  public void testWrongProtocol() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.0
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 400 Bad Request
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 30
          
          Non-supported protocol version""", response);
      assertStreamEnded(socket);
    }

  }

  @Test
  public void testWrongMethod() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          NOT_REAL_METHOD /echo HTTP/1.0
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """;
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 501 Not Implemented
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 25
          
          Non-supported HTTP method""", response);
      assertStreamEnded(socket);
    }
  }

  @Test
  public void testIdle() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      Thread.sleep(MAX_IO_READ_TIME_MLS + 1_000); // read timeout on server side
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 408 Request Timeout
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 14
          
          Read timed out""", response);
      assertStreamEnded(socket);
    }
  }

  @Test
  public void testTooManyHeaders() throws IOException, InterruptedException {
    int headersToCreate = 1_000;
    List<String> headers = new ArrayList<>();
    for (int i = 0; i < headersToCreate; i++) {
      headers.add("X-Header-%s: %s".formatted(i, i));
    }
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String requestMissingStartLine = """
          GET /echo HTTP/1.1
          Host: 127.0.0.1
          %s
          Connection: close
          
          """.replace("\n", "\r\n")
          .formatted(String.join("\r\n", headers));
      socket.getOutputStream()
          .write(httpFriendly(requestMissingStartLine).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 413 Payload Too Large
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 31
          
          Can't read more than %s bytes""".formatted(MAX_BYTES_TO_READ), response);
      assertStreamEnded(socket);
    }
  }

  @Test
  public void testBigBody() throws IOException, InterruptedException {
    String body = "Hello World! ".repeat(1_000);
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: text/plain
          Content-Length: %s
          
          %s""".formatted(body.length(), body).replace("\n", "\r\n");
      socket.getOutputStream().write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 413 Payload Too Large
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 31
          
          Can't read more than %s bytes""".formatted(MAX_BYTES_TO_READ), response);
      assertStreamEnded(socket);
    }
  }


  @Test
  public void testSendWrongContentLength() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      // content len > actual body
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: application/json
          Content-Length: 512
          
          {
            "username": "alice",
            "password": "secret123"
          }""";
      socket.getOutputStream()
          .write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(500); // wait until server reacts
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = reader.readAllAsString();
      assertEqualsHttpFriendly("""
          HTTP/1.1 408 Request Timeout
          content-type: text/plain; charset=UTF-8
          cache-control: no-store
          content-length: 14
          
          Read timed out""", response);
      assertStreamEnded(socket);
    }
  }

  @Test
  public void testSendBodySuperSlow() throws IOException, InterruptedException {
    int contentLen = 100;
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: text/plain
          Content-Length: %s
          
          """.formatted(contentLen).replace("\n", "\r\n");
      socket.getOutputStream()
          .write(httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET));
      for (int i = 0; i < contentLen; i++) {
        // send one byte super slow
        Thread.sleep(500);
        try {
          socket.getOutputStream().write("X".getBytes(Constants.DEFAULT_CHARSET));
        } catch (SocketException e) {
          break;
        }
      }
    }
  }

  @Test
  public void testSendHeadersSuperSlow() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.0
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """;
      var requestBytes = httpFriendly(request).getBytes(Constants.DEFAULT_CHARSET);
      for (byte requestByte : requestBytes) {
        // send one byte super slow
        Thread.sleep(500);
        try {
          socket.getOutputStream().write(requestByte);
        } catch (SocketException e) {
          break;
        }
      }
    }
  }

  private void assertStreamEnded(Socket socket) throws IOException {
    assertEquals(-1, socket.getInputStream().read());
  }

  private static String httpFriendly(String text) {
    return text.replace("\n", "\r\n");
  }

  public static void assertEqualsHttpFriendly(final @NonNull String expected,
      final @NonNull String actual) {
    assertEquals(httpFriendly(expected), actual);
  }

}
