package com.demo.web.reader;

import static com.demo.web.util.Constants.DEFAULT_CHARSET;

import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpResponseCode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * HTTP-specific input stream wrapper that limits read time and incoming data. Not thread-safe!
 */
public class HTTPLimitedInputStream {

  private final InputStream inputStream;
  private final int maxBytesToRead;
  private final int maxIOReadTimeMls;
  @Getter
  private int bytesRead;

  /**
   * Creates LimitedInputStream instance
   *
   * @param inputStream      original input stream. Normally a socket input stream.
   * @param maxBytesToRead   maximum volume of data to read from the input stream
   * @param maxIOReadTimeMls max total read time in milliseconds
   */
  @Builder
  public HTTPLimitedInputStream(
      final @NonNull InputStream inputStream,
      final int maxBytesToRead,
      final int maxIOReadTimeMls) {
    this.inputStream = inputStream;
    if (maxBytesToRead <= 0) {
      throw new IllegalArgumentException("Can't read %s bytes".formatted(maxBytesToRead));
    }
    this.maxBytesToRead = maxBytesToRead;
    if (maxIOReadTimeMls <= 0) {
      throw new IllegalArgumentException("Invalid read time-out: %s".formatted(maxBytesToRead));
    }
    this.maxIOReadTimeMls = maxIOReadTimeMls;
  }


  public int read() throws IOException {
    if (bytesRead >= maxBytesToRead) {
      throw new HTTPProtocolException("Can't read more than %s bytes".formatted(maxBytesToRead),
          HttpResponseCode.PAYLOAD_TOO_LARGE);
    }
    int i = inputStream.read();
    bytesRead++;
    return i;
  }


  public String readLine() throws IOException {
    var byteArrayOutputStream = new ByteArrayOutputStream();
    int b;
    long startTimeMls = System.currentTimeMillis();
    while ((b = this.read()) != -1) {
      if (System.currentTimeMillis() - startTimeMls > maxIOReadTimeMls) {
        // this might happen if producer is too slow
        throw new HTTPProtocolException("Read time-out", HttpResponseCode.REQUEST_TIMEOUT);
      }
      if (b == '\r') {
        continue;
      } else if (b == '\n') {
        break;
      }
      byteArrayOutputStream.write(b);
    }

    return byteArrayOutputStream.size() == 0
        ? null
        : byteArrayOutputStream.toString(DEFAULT_CHARSET);
  }

  public String readRemainingBytes(final int contentLen) throws IOException {
    //
    var byteArrayOutputStream = new ByteArrayOutputStream();
    long startTimeMls = System.currentTimeMillis();
    for (int i = 0; i < contentLen; i++) {
      if (System.currentTimeMillis() - startTimeMls > maxIOReadTimeMls) {
        // this might happen if producer is too slow
        throw new HTTPProtocolException("Request body read time-out",
            HttpResponseCode.REQUEST_TIMEOUT);
      }
      byteArrayOutputStream.write((byte) this.read());
    }
    return byteArrayOutputStream.toString(DEFAULT_CHARSET);
  }

  @Override
  public String toString() {
    return inputStream.toString();
  }
}
