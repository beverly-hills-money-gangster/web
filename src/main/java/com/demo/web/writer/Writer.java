package com.demo.web.writer;

import java.io.IOException;
import java.io.OutputStream;

public interface Writer<T> {

  void write(OutputStream outputStream, T object) throws IOException;
}
