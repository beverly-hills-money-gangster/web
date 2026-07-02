package com.demo.web.validation;

import static com.demo.web.util.Constants.CONTENT_LEN_HEADER;

import com.demo.annotation.Component;
import com.demo.web.model.HttpHeaders;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;

@Component
public class ContentLenHttpHeaderValidator implements Validator<HttpHeaders> {


  @Override
  public void validate(final @NonNull HttpHeaders headers) {
    var contentLengthValue = headers.getOne(CONTENT_LEN_HEADER)
        .orElseThrow(() -> new IllegalArgumentException("content-length header is missing"));
    if (!NumberUtils.isParsable(contentLengthValue)) {
      throw new IllegalArgumentException("Non-numeric content-length");
    }
    long contentLength = Long.parseLong(contentLengthValue);
    if (contentLength < 0) {
      throw new IllegalArgumentException("Negative content-length");
    }
  }
}
