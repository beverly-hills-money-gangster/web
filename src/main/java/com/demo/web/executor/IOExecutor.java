package com.demo.web.executor;

import java.net.Socket;
import java.util.function.Consumer;
import lombok.NonNull;

/**
 * IO (socket) logic executor
 */
public abstract class IOExecutor {

  public void execute(final @NonNull Socket socket, final @NonNull Consumer<Socket> consumer) {
    executeImpl(() -> consumer.accept(socket));
  }

  /**
   * Defines how IO logic has to be executed.
   * For example, one IO operation -> one thread, or one IO operation -> virtual thread
   */
  protected abstract void executeImpl(Runnable runnable);
}
