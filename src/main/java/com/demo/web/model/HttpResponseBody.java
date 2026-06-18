package com.demo.web.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

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
