package com.demo.web.protocol;

import com.demo.annotation.Component;
import com.demo.web.exception.ExceptionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

@Component
public class ExceptionCaptureListener extends ExceptionListener {

  @Getter
  private final List<Throwable> errors = new CopyOnWriteArrayList<>();

  @Override
  protected void listenImpl(Throwable e) {
    errors.add(e);
  }

  public void clear() {
    errors.clear();
  }

}
