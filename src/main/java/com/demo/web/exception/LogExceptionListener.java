package com.demo.web.exception;

import com.demo.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO get rid of that maybe?
@Component
public class LogExceptionListener extends ExceptionListener {

  private static final Logger LOG = LoggerFactory.getLogger(LogExceptionListener.class);

  @Override
  protected void listenImpl(Throwable e) {
    LOG.error("Exception occurred", e);
  }
}
