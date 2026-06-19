package com.demo.web.executor;

import com.demo.annotation.Component;
import com.demo.annotation.Profile;

@Profile(profiles = {"virtualThreadExecutor", "default"})
@Component
public class VirtualThreadSocketExecutor extends SocketExecutor {

  @Override
  protected void executeImpl(Runnable runnable) {
    Thread.ofVirtual().name("server-socket").start(runnable);
  }

}
