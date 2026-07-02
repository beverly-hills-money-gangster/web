package com.demo.web.reader;

import static com.demo.web.util.Constants.BODY_METHODS;
import static com.demo.web.util.Constants.CONNECTION_HEADER;
import static com.demo.web.util.Constants.CONTENT_LEN_HEADER;
import static com.demo.web.util.Constants.HTTP_1_1;
import static com.demo.web.util.Constants.START_LINE_ELEMENTS;

import com.demo.annotation.Component;
import com.demo.web.config.WebServerConfig;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpMethod;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpRequestHeaders;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.model.RequestURI;
import com.demo.web.validation.ContentLenHttpHeaderValidator;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Low-level HTTP request reader. It takes socket input stream and reads into HttpRequest object
 */
@Component
@RequiredArgsConstructor
public class HttpRequestReader implements Reader<HttpRequest> {

  private static final Logger LOG = LoggerFactory.getLogger(HttpRequestReader.class);
  private static final Set<String> SUPPORTED_METHODS
      = Arrays.stream(HttpMethod.values()).map(Enum::name).collect(Collectors.toSet());


  private final ContentLenHttpHeaderValidator contentLenHttpHeaderValidator;
  private final WebServerConfig webServerConfig;

  /**
   * Reads socket input stream into HttpRequest object
   */
  @Override
  public HttpRequest read(final InputStream inputStream) throws HTTPProtocolException {
    var limitedStream = HTTPLimitedInputStream.builder().inputStream(inputStream)
        .maxBytesToRead(webServerConfig.getMaxBytesToRead())
        .maxIOReadTimeMls(webServerConfig.getMaxIOReadTimeMls()).build();
    try {
      var builder = HttpRequest.builder();
      var headers = new HttpRequestHeaders();
      String line;
      var readState = ReadState.START_LINE;
      int linesRead = 0;
      HttpMethod method = null;
      // TODO why 2 TCP connections open and one of them is empty?
      while ((line = limitedStream.readLine()) != null) {
        linesRead++;
        if (StringUtils.isBlank(line)) {
          break;
        }
        switch (readState) {
          case START_LINE -> {
            var lineSplit = line.split(" ");
            if (lineSplit.length != START_LINE_ELEMENTS) {
              throw new HTTPProtocolException("Invalid HTTP start-line",
                  HttpResponseCode.BAD_REQUEST);
            }
            var methodText = lineSplit[0];
            if (!SUPPORTED_METHODS.contains(methodText)) {
              throw new HTTPProtocolException("Non-supported HTTP method",
                  HttpResponseCode.NOT_IMPLEMENTED);
            }
            method = HttpMethod.valueOf(methodText);
            builder.method(method).uri(new RequestURI(lineSplit[1]));
            String version = lineSplit[2];
            if (!HTTP_1_1.equals(version)) {
              throw new HTTPProtocolException(
                  "Non-supported protocol version", HttpResponseCode.BAD_REQUEST);
            }
            readState = ReadState.HEADERS;
          }
          case HEADERS -> {
            var splitHeader = line.split(":", 2);
            if (splitHeader.length < 2) {
              throw new HTTPProtocolException("Invalid HTTP header", HttpResponseCode.BAD_REQUEST);
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
        builder.body(limitedStream.readRemainingBytes(contentLen));
      }
      builder.keepAlive(
          headers.getOne(CONNECTION_HEADER).map(s -> !"close".equals(s)).orElse(true));
      LOG.debug("Bytes read {}", limitedStream.getBytesRead());
      return builder.build();
    } catch (Exception e) {
      if (e instanceof HTTPProtocolException protocolException) {
        throw protocolException;
      }
      var message = Objects.toString(e.getMessage(), "Error occurred");
      var responseCode = e instanceof IllegalArgumentException ? HttpResponseCode.BAD_REQUEST :
          e instanceof SocketTimeoutException ? HttpResponseCode.REQUEST_TIMEOUT
              : HttpResponseCode.INTERNAL_SERVER_ERROR;
      throw new HTTPProtocolException(message, e, responseCode);
    }
  }

}
