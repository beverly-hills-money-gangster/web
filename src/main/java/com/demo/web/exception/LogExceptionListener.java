package com.demo.web.exception;

import com.demo.annotation.Component;
import java.net.SocketTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic logging exception listener
 */
@Component
public class LogExceptionListener extends ExceptionListener {

  private static final Logger LOG = LoggerFactory.getLogger(LogExceptionListener.class);

  @Override
  protected void listenImpl(Throwable e) {
    if (e instanceof SocketTimeoutException) {
      LOG.debug("Timeout", e);
    } else {
      LOG.error("Exception occurred", e);
    }
  }
}
