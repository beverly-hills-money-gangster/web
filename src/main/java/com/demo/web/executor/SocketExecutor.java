package com.demo.web.executor;

import java.net.Socket;
import java.util.function.Consumer;
import lombok.NonNull;

public abstract class SocketExecutor {

  public void execute(final @NonNull Socket socket, final @NonNull Consumer<Socket> consumer) {
    executeImpl(() -> consumer.accept(socket));
  }

  protected abstract void executeImpl(Runnable runnable);
}
