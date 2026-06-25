package com.demo.web.bootstrap;

import com.demo.decorator.Decorator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class WebContainerBootstrap {

  private @NonNull
  final Class<?> source;
  @Builder.Default
  private final Map<Class<?>, Decorator> decorators = new HashMap<>();
  @Builder.Default
  private final Set<String> profiles = new HashSet<>();
}