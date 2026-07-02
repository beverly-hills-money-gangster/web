package com.demo.web.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * HTTP response body in a form of a stream. InputStream is used so we don't have to copy binary
 * data from-to heap if big files are processed. The stream is to be closed by the calling side.
 */
@Builder
@Getter
public class HttpResponseBody implements Closeable {

  @NonNull
  private final InputStream stream;

  private final long length;

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
