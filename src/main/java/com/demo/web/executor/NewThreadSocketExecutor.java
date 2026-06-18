package com.demo.web.executor;

import com.demo.annotation.Component;
import com.demo.annotation.Profile;

@Profile(profiles = "newThreadExecutor")
@Component
public class NewThreadSocketExecutor extends SocketExecutor {

  @Override
  protected void executeImpl(Runnable runnable) {
    new Thread(runnable).start();
  }
}
