package com.demo.web.validation;

import static com.demo.web.util.Constants.CONTENT_LEN_HEADER;

import com.demo.annotation.Component;
import com.demo.web.model.HttpHeaders;
import org.apache.commons.lang3.math.NumberUtils;

@Component
public class ContentLenHttpHeaderValidator implements Validator<HttpHeaders> {


  @Override
  public void validate(HttpHeaders headers) {
    var contentLengthValue = headers.getOne(CONTENT_LEN_HEADER)
        .orElseThrow(() -> new IllegalArgumentException(
            "%s header is missing".formatted(CONTENT_LEN_HEADER)));
    if (!NumberUtils.isParsable(contentLengthValue)) {
      throw new IllegalArgumentException(
          "Non-numeric %s".formatted(CONTENT_LEN_HEADER));
    }
    long contentLength = Long.parseLong(contentLengthValue);
    if (contentLength < 0) {
      throw new IllegalArgumentException("Negative %s".formatted(CONTENT_LEN_HEADER));
    }
  }
}
