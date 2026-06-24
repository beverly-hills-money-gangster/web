package com.demo.web.validation;

import static com.demo.web.util.Constants.CONTENT_LEN_HEADER;

import com.demo.annotation.Component;
import com.demo.web.model.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

@Component
public class ContentLenHttpHeaderValidator implements Validator<HttpHeaders> {


  @Override
  public void validate(HttpHeaders headers) {
    var contentLengthValue = headers.getOne(CONTENT_LEN_HEADER)
        .orElseThrow(() -> new IllegalArgumentException(
            "%s header is missing".formatted(CONTENT_LEN_HEADER)));
    if (!StringUtils.isNumeric(contentLengthValue)) {
      throw new IllegalArgumentException(
          "Invalid %s. See: '%s'".formatted(CONTENT_LEN_HEADER, contentLengthValue));
    }
  }
}
