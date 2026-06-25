package com.demo.web.reader;

import static com.demo.web.util.Constants.BODY_METHODS;
import static com.demo.web.util.Constants.CONNECTION_HEADER;
import static com.demo.web.util.Constants.CONTENT_LEN_HEADER;
import static com.demo.web.util.Constants.DEFAULT_CHARSET;
import static com.demo.web.util.Constants.HTTP_1_1;
import static com.demo.web.util.Constants.START_LINE_ELEMENTS;

import com.demo.annotation.Component;
import com.demo.web.config.WebConfig;
import com.demo.web.model.HttpHeaders;
import com.demo.web.model.HttpMethod;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.RequestURI;
import com.demo.web.validation.ContentLenHttpHeaderValidator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class HttpRequestReader implements Reader<HttpRequest> {

  private static final Set<String> SUPPORTED_METHODS
      = Arrays.stream(HttpMethod.values()).map(Enum::name).collect(Collectors.toSet());

  private static final Logger LOG = LoggerFactory.getLogger(HttpRequestReader.class);
  private final ContentLenHttpHeaderValidator contentLenHttpHeaderValidator;
  private final WebConfig webConfig;

  @Override
  public HttpRequest read(final InputStream inputStream) throws IOException {
    var limitedStream = new LimitedInputStream(inputStream, webConfig.getMaxBytesToRead());
    try {
      var builder = HttpRequest.builder();
      var headers = new HttpHeaders();
      String line;
      var readState = ReadState.START_LINE;
      int linesRead = 0;
      HttpMethod method = null;
      // TODO why 2 TCP connections open and one of them is empty?
      while ((line = readLine(limitedStream)) != null) {
        linesRead++;
        if (StringUtils.isBlank(line)) {
          break;
        }
        switch (readState) {
          case START_LINE -> {
            var lineSplit = line.split(" ");
            if (lineSplit.length != START_LINE_ELEMENTS) {
              throw new IOException("Invalid HTTP start-line. See %s".formatted(line));
            }
            var methodText = lineSplit[0];
            if (!SUPPORTED_METHODS.contains(methodText)) {
              throw new IOException("Non-supported HTTP method");
            }
            method = HttpMethod.valueOf(methodText);
            builder.method(method).uri(new RequestURI(lineSplit[1]));
            String version = lineSplit[2];
            if (!HTTP_1_1.equals(version)) {
              throw new IOException("Non-supported protocol version. See %s".formatted(version));
            }
            readState = ReadState.HEADERS;
          }
          case HEADERS -> {
            var splitHeader = line.split(":", 2);
            if (splitHeader.length < 2) {
              throw new IOException("Invalid HTTP header. See %s".formatted(line));
            }
            var headerName = splitHeader[0].trim().toLowerCase(Locale.ENGLISH);
            var headerValue = splitHeader[1].trim();
            headers.add(headerName, headerValue);
          }
        }
      }
      if (linesRead == 0) {
        return null; // TODO why this happens?
      }
      builder.headers(headers);
      if (BODY_METHODS.contains(method)) {
        contentLenHttpHeaderValidator.validate(headers);
        int contentLen = headers.getOne(CONTENT_LEN_HEADER).map(Integer::parseInt).get();
        builder.body(readBody(contentLen, limitedStream));
      }
      builder.keepAlive(
          headers.getOne(CONNECTION_HEADER).map(s -> !"close".equals(s)).orElse(true));
      LOG.debug("Bytes read {}", limitedStream.getBytesRead());
      return builder.build();
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  private String readLine(LimitedInputStream in) throws IOException {
    var byteArrayOutputStream = new ByteArrayOutputStream();
    int b;
    long startTimeMls = System.currentTimeMillis();
    while ((b = in.read()) != -1) {
      if (System.currentTimeMillis() - startTimeMls > webConfig.getMaxIOReadTimeMls()) {
        // this might happen if producer is too slow
        throw new IOException("Read time-out");
      }
      if (b == '\r') {
        continue;
      } else if (b == '\n') {
        break;
      }
      byteArrayOutputStream.write(b);
    }

    return byteArrayOutputStream.size() == 0
        ? null
        : byteArrayOutputStream.toString(DEFAULT_CHARSET);
  }

  private String readBody(int contentLen, LimitedInputStream in) throws IOException {
    var byteArrayOutputStream = new ByteArrayOutputStream();
    long startTimeMls = System.currentTimeMillis();
    for (int i = 0; i < contentLen; i++) {
      if (System.currentTimeMillis() - startTimeMls > webConfig.getMaxIOReadTimeMls()) {
        // this might happen if producer is too slow
        throw new IOException("Request body read time-out");
      }
      byteArrayOutputStream.write((byte) in.read());
    }
    return byteArrayOutputStream.toString(DEFAULT_CHARSET);
  }
}
