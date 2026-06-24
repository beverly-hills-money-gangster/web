package com.demo.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExceptionListener {

  private static final Logger LOG = LoggerFactory.getLogger(ExceptionListener.class);

  public final void listen(final Throwable exception) {
    try {
      listenImpl(exception);
    } catch (Exception innerException) {
      LOG.error("Can't handle exception", innerException);
    }
  }

  protected abstract void listenImpl(final Throwable e);
}
