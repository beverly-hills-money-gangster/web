package com.demo.web.factory;

import static com.demo.web.util.Constants.DEFAULT_CHARSET;
import static com.demo.web.util.Constants.NO_CACHE_STORE;

import com.demo.annotation.Component;
import com.demo.web.exception.WebException;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpHeaders;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseBody;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.util.Constants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class HttpResponseFactory {

  private static final Logger LOG = LoggerFactory.getLogger(HttpResponseFactory.class);

  private final ObjectMapperFactory objectMapperFactory;

  public HttpResponse text(HttpResponseCode code) {
    return text(code.getMsg(), code);
  }

  public HttpResponse redirect(String to) {
    return HttpResponse.builder().code(HttpResponseCode.MOVED_PERMANENTLY)
        .headers(new HttpHeaders()
            .add("location", to)
            .add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE))
        .build();
  }

  public HttpResponse text(String text, HttpResponseCode code) {
    var textBytes = text.getBytes(DEFAULT_CHARSET);
    return HttpResponse.builder().code(code)
        .body(HttpResponseBody.builder().stream(new ByteArrayInputStream(textBytes))
            .length(textBytes.length).build())
        .headers(new HttpHeaders()
            .addContentType(HttpContentType.TEXT)
            .add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE))
        .build();
  }

  public HttpResponse text(Throwable t, HttpResponseCode code) {
    return text(ExceptionUtils.getMessage(t), code);
  }

  public HttpResponse json(Object json, HttpResponseCode code) {
    try {
      byte[] jsonBytes = objectMapperFactory.create().writeValueAsString(json)
          .getBytes(DEFAULT_CHARSET);
      return HttpResponse.builder().code(code)
          .body(HttpResponseBody.builder().stream(new ByteArrayInputStream(jsonBytes))
              .length(jsonBytes.length).build())
          .headers(new HttpHeaders().addContentType(HttpContentType.JSON)
              .add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE))
          .build();
    } catch (Exception e) {
      throw new WebException("Can't generate json", e);
    }
  }

  public HttpResponse html(String html, HttpResponseCode code) {
    var htmlBytes = html.getBytes(DEFAULT_CHARSET);
    return HttpResponse.builder().code(code)
        .body(HttpResponseBody.builder().stream(new ByteArrayInputStream(htmlBytes))
            .length(htmlBytes.length).build())
        .headers(new HttpHeaders().addContentType(HttpContentType.HTML)
            .add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE))
        .build();
  }

  public HttpResponse resource(String fileName,
      HttpResponseCode code) {
    var fullFileName = "/static/" + fileName;
    InputStream in = null;
    try {
      URL url = HttpResponseFactory.class.getResource(fullFileName);
      if (url == null) {
        return text("File %s not found".formatted(fullFileName), HttpResponseCode.NOT_FOUND);
      }
      var conn = url.openConnection();
      long contentLength = conn.getContentLengthLong();
      if (contentLength < 0) {
        return text("Can't read file %s. Invalid metadata.".formatted(fullFileName),
            HttpResponseCode.INTERNAL_SERVER_ERROR);
      }

      in = HttpResponseFactory.class.getResourceAsStream(fullFileName);
      if (in == null) {
        return text("File %s not found".formatted(fullFileName), HttpResponseCode.NOT_FOUND);
      }
      var headers = new HttpHeaders();
      headers.addContentType(
          HttpContentType.get(URLConnection.guessContentTypeFromName(fullFileName)));
      return HttpResponse.builder().code(code)
          .body(HttpResponseBody.builder().stream(in).length(contentLength).build())
          .headers(headers)
          .build();
    } catch (Exception e) {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          LOG.error("Can't close file {}", fullFileName, e);
        }
      }
      throw new WebException("Can't get resource %s".formatted(fullFileName), e);
    }
  }
}
