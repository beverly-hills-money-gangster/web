package com.demo.web.validation;

import static com.demo.web.util.Constants.CONTENT_LEN_HEADER;
import static com.demo.web.util.Constants.MAX_CONTENT_LENGTH_BYTES;

import com.demo.annotation.Component;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpHeaders;
import org.apache.commons.lang3.StringUtils;

@Component
public class ContentLenHttpHeaderValidator implements Validator<HttpHeaders> {

  @Override
  public void validate(HttpHeaders headers) {
    var contentLengthValue = headers.getOne(CONTENT_LEN_HEADER)
        .orElseThrow(
            () -> new HTTPProtocolException("%s header is missing".formatted(CONTENT_LEN_HEADER)));
    if (!StringUtils.isNumeric(contentLengthValue)) {
      throw new HTTPProtocolException(
          "Invalid %s. See: '%s'".formatted(CONTENT_LEN_HEADER, contentLengthValue));
    }
    var contentLen = Integer.parseInt(contentLengthValue);
    if (contentLen > MAX_CONTENT_LENGTH_BYTES) {
      throw new HTTPProtocolException(
          "Body is too long. See %s: %s".formatted(CONTENT_LEN_HEADER, contentLen));
    }
  }
}
