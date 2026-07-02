package com.demo.web.executor;

import com.demo.annotation.Component;

/**
 * Default virtual thread executor for all IO operations(read request, write response)
 */
@Component
public class VirtualThreadIOExecutor extends IOExecutor {

  @Override
  protected void executeImpl(Runnable runnable) {
    Thread.ofVirtual().name("server-socket").start(runnable);
  }

}
