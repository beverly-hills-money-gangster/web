package com.demo.web;

import com.demo.container.Container;
import com.demo.container.ContainerInitializer;
import lombok.NonNull;

// TODO add cookies
// TODO add javadoc
// TODO add readme
// TODO add circle ci integration
// TODO add jitpack integration
// TODO test URI patterns too
// TODO test URI request params
public class WebContainer {

  public static Container init(
      final @NonNull Class<?> source,
      final @NonNull String... profiles) {
    return new ContainerInitializer(getWebProfiles(profiles)).init(WebContainer.class, source);
  }

  private static String[] getWebProfiles(final @NonNull String[] profiles) {
    // copy all profiles + add default
    var newProfiles = new String[profiles.length + 1];
    System.arraycopy(profiles, 0, newProfiles, 0, profiles.length);
    newProfiles[newProfiles.length - 1] = "default";
    return newProfiles;
  }
}
