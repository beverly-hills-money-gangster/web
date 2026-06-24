package com.demo.web.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demo.web.WebTest;
import com.demo.web.util.Constants;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ProtocolTest extends WebTest {

  @Test
  public void testMissingStartLine() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """.replace("\n", "\r\n");
      socket.getOutputStream().write(request.getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(1_000); // wait until server reacts
      assertEquals(-1, socket.getInputStream().read());
    }
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ioe && ioe.getMessage()
            .startsWith("Invalid HTTP start-line"))));
  }


  @Test
  public void testMissingContentLength() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: application/json
          
          {
            "username": "alice",
            "password": "secret123"
          }""".replace("\n", "\r\n");
      socket.getOutputStream()
          .write(request.getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(1_000); // wait until server reacts
      assertEquals(-1, socket.getInputStream().read());
    }
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ex
            && ex.getCause() != null && ex.getCause().getMessage()
            .startsWith("content-length header is missing"))));
  }

  @Test
  public void testWrongProtocol() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.0
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """.replace("\n", "\r\n");
      socket.getOutputStream().write(request.getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(1_000); // wait until server reacts
      assertEquals(-1, socket.getInputStream().read());
    }
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ioe && ioe.getMessage()
            .startsWith("Non-supported protocol version"))));
  }

  @Test
  public void testIdle() throws IOException, InterruptedException {
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      Thread.sleep(Constants.MAX_IO_READ_TIME_MLS + 1_000); // read timeout on server side
      assertEquals(-1, socket.getInputStream().read());
    }
  }

  @Test
  public void testTooManyHeaders() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    int headersToCreate = 1_000_000;
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
      var ex = assertThrows(SocketException.class, () -> socket.getOutputStream()
          .write(requestMissingStartLine.getBytes(Constants.DEFAULT_CHARSET)));
      assertEquals("Connection reset by peer", ex.getMessage());
    }
    Thread.sleep(1_000);
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ioe && ioe.getMessage()
            .equals("Can't read more than %s bytes".formatted(Constants.MAX_BYTES_TO_READ)))));
  }

  @Test
  public void testBigBody() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    String body = "Hello World! ".repeat(1_000_000);
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: text/plain
          Content-Length: %s
          
          %s""".formatted(body.length(), body).replace("\n", "\r\n");
      var ex = assertThrows(SocketException.class, () -> socket.getOutputStream()
          .write(request.getBytes(Constants.DEFAULT_CHARSET)));
      assertEquals("Connection reset by peer", ex.getMessage());
    }
    Thread.sleep(1_000); // wait until server reacts
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ex && ex.getMessage()
            .equals("Can't read more than %s bytes".formatted(Constants.MAX_BYTES_TO_READ)))));
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
          Content-Length: 4098
          
          {
            "username": "alice",
            "password": "secret123"
          }""".replace("\n", "\r\n");
      socket.getOutputStream()
          .write(request.getBytes(Constants.DEFAULT_CHARSET));
      Thread.sleep(1_000); // wait until server reacts
      assertEquals(-1, socket.getInputStream().read());
    }
  }

  @Test
  public void testSendBodySuperSlow() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    int contentLen = 4098;
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          POST /echo HTTP/1.1
          Host: 127.0.0.1
          Content-Type: text/plain
          Content-Length: %s
          
          """.formatted(contentLen).replace("\n", "\r\n");
      socket.getOutputStream()
          .write(request.getBytes(Constants.DEFAULT_CHARSET));
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
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ex && ex.getMessage()
            .equals("Request body read time-out"))));
  }

  @Test
  public void testSendHeadersSuperSlow() throws IOException, InterruptedException {
    var exceptionCaptureListener = getComponent(ExceptionCaptureListener.class);
    try (Socket socket = new Socket("127.0.0.1", PORT)) {
      socket.setSoTimeout(10_000);
      String request = """
          GET /echo HTTP/1.0
          Host: 127.0.0.1
          User-Agent: MyApp/1.0
          Accept: application/json
          Connection: close
          
          """.replace("\n", "\r\n");
      var requestBytes = request.getBytes(Constants.DEFAULT_CHARSET);
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
    assertTrue(exceptionCaptureListener.getErrors().stream().anyMatch(
        throwable -> (throwable instanceof IOException ex && ex.getMessage()
            .equals("Read time-out"))));
  }

}
