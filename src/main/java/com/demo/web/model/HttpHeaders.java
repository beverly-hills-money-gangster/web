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

@ToString
public class HttpHeaders implements HttpHeadersReader {

  private final Map<String, Set<String>> headers = new HashMap<>();

  public HttpHeaders add(@NonNull final String header, @NonNull final String value) {
    headers.computeIfAbsent(header.toLowerCase(Locale.ENGLISH), (val) -> new HashSet<>())
        .add(value);
    return this;
  }

  public HttpHeaders addContentType(HttpContentType contentType) {
    return add(Constants.CONTENT_TYPE_HEADER, contentType.getType());
  }

  @Override
  public Set<String> get(final @NonNull String header) {
    return headers.get(header.toLowerCase(Locale.ENGLISH));
  }

  @Override
  public Iterable<Map.Entry<String, Set<String>>> readHeaders() {
    return headers.entrySet();
  }

  @Override
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
