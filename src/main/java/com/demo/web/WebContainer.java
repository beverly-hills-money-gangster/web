package com.demo.web;

import com.demo.container.Container;
import com.demo.container.ContainerInitializer;
import com.demo.web.config.WebContainerConfig;
import java.util.Set;
import lombok.NonNull;

/**
 * Main web container for the framework
 */
public class WebContainer {

  public static Container init(final @NonNull WebContainerConfig config) {
    var initializer = new ContainerInitializer(config.getProfiles().toArray(String[]::new));
    config.getDecorators().forEach(initializer::addDecorator);
    return initializer.init(WebContainer.class, config.getSource());
  }

  public static Container init(final Class<?> source, Set<String> profiles) {
    return init(WebContainerConfig.builder().source(source).profiles(profiles).build());
  }


}
