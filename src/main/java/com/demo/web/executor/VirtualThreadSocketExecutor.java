package com.demo.web.executor;

import com.demo.annotation.Component;

@Component
public class VirtualThreadSocketExecutor extends SocketExecutor {

  @Override
  protected void executeImpl(Runnable runnable) {
    Thread.ofVirtual().name("server-socket").start(runnable);
  }

}
