package com.demo.web.model;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;


public interface HttpHeadersReader {

  Set<String> get(final @NonNull String header);

  Iterable<Map.Entry<String, Set<String>>> readHeaders();

  Optional<String> getOne(final @NonNull String header);
}
