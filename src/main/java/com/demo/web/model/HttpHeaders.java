package com.demo.web.model;

import com.demo.web.util.Constants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.ToString;

/**
 * Basic HTTP headers storage that is common for both responses and requests
 */
@ToString
public abstract class HttpHeaders {

  private final Map<String, Set<String>> headers = new HashMap<>();

  public void add(@NonNull final String header, @NonNull final String value) {
    headers.computeIfAbsent(header.toLowerCase(Locale.ENGLISH), (_) -> new HashSet<>())
        .add(value);
  }

  public void remove(@NonNull final String header) {
    headers.remove(header);
  }

  public void replace(@NonNull final String header, @NonNull final Set<String> values) {
    headers.replace(header, values);
  }

  public void replace(@NonNull final String header, String value) {
    headers.replace(header, Set.of(value));
  }

  public void addContentType(HttpContentType contentType) {
    add(Constants.CONTENT_TYPE_HEADER, contentType.getType());
  }

  public Set<String> get(final @NonNull String header) {
    return headers.get(header.toLowerCase(Locale.ENGLISH));
  }

  public Iterable<Map.Entry<String, Set<String>>> readHeaders() {
    return headers.entrySet();
  }

  public Optional<String> getOne(final @NonNull String header) {
    var values = get(header);
    if (values == null || values.isEmpty()) {
      return Optional.empty();
    } else if (values.size() > 1) {
      throw new IllegalArgumentException(
          "Only one value expected for header %s. Values: %s".formatted(header, values));
    }
    return values.stream().findFirst();
  }
}
