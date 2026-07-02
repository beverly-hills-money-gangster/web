package com.demo.web;

import com.demo.container.Container;
import com.demo.container.ContainerInitializer;
import com.demo.web.config.WebContainerConfig;
import java.util.Set;
import lombok.NonNull;

// TODO add mustache
// TODO add javadoc
// TODO add readme
// TODO add circle ci integration
// TODO add jitpack integration
public class WebContainer {

  public static Container init(final @NonNull WebContainerConfig bootstrap) {
    var initializer = new ContainerInitializer(bootstrap.getProfiles().toArray(String[]::new));
    bootstrap.getDecorators().forEach(initializer::addDecorator);
    return initializer.init(WebContainer.class, bootstrap.getSource());
  }

  public static Container init(final Class<?> source, Set<String> profiles) {
    return init(WebContainerConfig.builder().source(source).profiles(profiles).build());
  }


}
