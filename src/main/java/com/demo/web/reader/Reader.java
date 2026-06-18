package com.demo.web.reader;


import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;

public interface Reader<T> {

  @Nullable
  T read(InputStream inputStream) throws IOException;
}
