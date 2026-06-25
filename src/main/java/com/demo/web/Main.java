package com.demo.web;

import com.demo.web.runner.ServerRunner;
import java.io.IOException;
import java.util.Set;

public class Main {

  static void main() throws IOException {
    try (var container = WebContainer.init(Main.class, Set.of("enableStaticFiles"))) {
      var serverRunner = container.getInstance(ServerRunner.class);
      serverRunner.start(8080);
    }
  }
}
