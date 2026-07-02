package com.demo.web.factory;

import static com.demo.web.util.Constants.DEFAULT_CHARSET;
import static com.demo.web.util.Constants.NO_CACHE_STORE;

import com.demo.annotation.Component;
import com.demo.web.exception.HTTPProtocolException;
import com.demo.web.model.HttpContentType;
import com.demo.web.model.HttpResponse;
import com.demo.web.model.HttpResponseBody;
import com.demo.web.model.HttpResponseCode;
import com.demo.web.model.HttpResponseHeaders;
import com.demo.web.util.Constants;
import com.github.mustachejava.Mustache;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Content-specific HTTP response factory
 */
@Component
@RequiredArgsConstructor
public class HttpResponseFactory {

  private static final Logger LOG = LoggerFactory.getLogger(HttpResponseFactory.class);

  private final ObjectMapperFactory objectMapperFactory;

  public HttpResponse text(HttpResponseCode code) {
    return text(code.getMsg(), code);
  }

  public HttpResponse redirect(String to) {
    var headers = new HttpResponseHeaders();
    headers.add("location", to);
    headers.add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE);
    return HttpResponse.builder().code(HttpResponseCode.MOVED_PERMANENTLY)
        .headers(headers)
        .build();
  }

  public HttpResponse text(String text, HttpResponseCode code) {
    var textBytes = text.getBytes(DEFAULT_CHARSET);
    var headers = new HttpResponseHeaders();
    headers.addContentType(HttpContentType.TEXT);
    headers.add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE);
    return HttpResponse.builder().code(code)
        .body(HttpResponseBody.builder().stream(new ByteArrayInputStream(textBytes))
            .length(textBytes.length).build())
        .headers(headers)
        .build();
  }

  public HttpResponse text(Throwable t, HttpResponseCode code) {
    return text(Objects.toString(t.getMessage(), "Error occurred"), code);
  }

  public HttpResponse json(Object json, HttpResponseCode code) {
    try {
      var headers = new HttpResponseHeaders();
      headers.addContentType(HttpContentType.JSON);
      headers.add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE);
      byte[] jsonBytes = objectMapperFactory.create().writeValueAsString(json)
          .getBytes(DEFAULT_CHARSET);
      return HttpResponse.builder().code(code)
          .body(HttpResponseBody.builder().stream(new ByteArrayInputStream(jsonBytes))
              .length(jsonBytes.length).build())
          .headers(headers)
          .build();
    } catch (Exception e) {
      throw new HTTPProtocolException("Can't generate json", e,
          HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }

  public HttpResponse html(
      final @NonNull Mustache mustache,
      final @NonNull Map<String, Object> context,
      final @NonNull HttpResponseCode code) {
    try (Writer writer = new StringWriter()) {
      mustache.execute(writer, context).flush();
      return html(writer.toString(), code);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public HttpResponse html(String html, HttpResponseCode code) {
    var htmlBytes = html.getBytes(DEFAULT_CHARSET);
    var headers = new HttpResponseHeaders();
    headers.addContentType(HttpContentType.HTML);
    headers.add(Constants.CACHE_CONTROL_HEADER, NO_CACHE_STORE);
    return HttpResponse.builder().code(code)
        .body(HttpResponseBody.builder().stream(new ByteArrayInputStream(htmlBytes))
            .length(htmlBytes.length).build())
        .headers(headers)
        .build();
  }

  public HttpResponse resource(String fileName,
      HttpResponseCode code) {
    var fullFileName = "/static/" + fileName;
    InputStream in = null;
    try {
      // get the resource
      URL url = HttpResponseFactory.class.getResource(fullFileName);
      if (url == null) {
        return text("File %s not found".formatted(fullFileName), HttpResponseCode.NOT_FOUND);
      }
      var conn = url.openConnection();
      // get content length
      long contentLength = conn.getContentLengthLong();
      if (contentLength < 0) {
        return text("Can't read file %s. Invalid metadata.".formatted(fullFileName),
            HttpResponseCode.INTERNAL_SERVER_ERROR);
      }

      in = HttpResponseFactory.class.getResourceAsStream(fullFileName);
      if (in == null) {
        return text("File %s not found".formatted(fullFileName), HttpResponseCode.NOT_FOUND);
      }
      var headers = new HttpResponseHeaders();
      // get content type
      var contentType = HttpContentType.get(URLConnection.guessContentTypeFromName(fullFileName));
      headers.addContentType(contentType.orElseThrow(() -> new HTTPProtocolException(
          "Can't get content type for file %s".formatted(fullFileName),
          HttpResponseCode.INTERNAL_SERVER_ERROR)));
      return HttpResponse.builder().code(code)
          .body(HttpResponseBody.builder().stream(in).length(contentLength).build())
          .headers(headers)
          .build();
    } catch (HTTPProtocolException e) {
      throw e;
    } catch (Exception e) {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          LOG.error("Can't close file {}", fullFileName, e);
        }
      }
      throw new HTTPProtocolException("Can't get resource %s".formatted(fullFileName), e,
          HttpResponseCode.INTERNAL_SERVER_ERROR);
    }
  }
}
