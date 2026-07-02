package com.demo.web.writer;

import com.demo.annotation.Component;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseBody;
import com.demo.web.util.Constants;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/**
 * HTTP response writer. Covers headers + body.
 */
@Component
public class HttpResponseWriter implements Writer<HttpResponse> {

  @Override
  public void write(OutputStream outputStream, HttpResponse response) throws IOException {
    try (response) {
      var respCode = response.getCode();
      var startLine = Constants.HTTP_1_1 + " " + respCode.getCode() + " " + respCode.getMsg();
      var builder = new StringBuilder();
      builder.append(startLine).append("\r\n");
      response.getHeaders().readHeaders().forEach(
          (entry) -> entry.getValue()
              .forEach(value -> builder.append(entry.getKey()).append(": ").append(value)
                  .append("\r\n")));
      builder.append(
          "%s: %s\r\n\r\n".formatted(Constants.CONTENT_LEN_HEADER,
              Optional.ofNullable(response.getBody())
                  .map(HttpResponseBody::getLength).orElse(0L)));
      outputStream.write(builder.toString().getBytes(Constants.DEFAULT_CHARSET));
      if (response.getBody() != null) {
        IOUtils.copy(response.getBody().getStream(), outputStream);
      }
      outputStream.flush();
    }
  }
}
