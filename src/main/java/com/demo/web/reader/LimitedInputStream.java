package com.demo.web.reader;

import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpResponseCode;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import lombok.Getter;


public class LimitedInputStream implements Closeable {

  private final InputStream inputStream;
  private final int maxBytesToRead;
  @Getter
  private int bytesRead;

  public LimitedInputStream(InputStream inputStream, int maxBytesToRead) {
    if (maxBytesToRead < 0) {
      throw new IllegalArgumentException(
          "Can't read negative number of bytes. See: %s".formatted(maxBytesToRead));
    }
    this.inputStream = inputStream;
    this.maxBytesToRead = maxBytesToRead;
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

  @Override
  public void close() throws IOException {
    inputStream.close();
  }

  @Override
  public String toString() {
    return inputStream.toString();
  }
}
