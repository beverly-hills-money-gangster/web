package com.demo.web.util;

import com.demo.web.model.HttpMethod;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Constants {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  public static final int MAX_BYTES_TO_READ =  1024 * 1024 * 10;
  public static final int MAX_IO_READ_TIME_MLS = 5_000;
  public static final int START_LINE_ELEMENTS = 3;
  public static final String CONTENT_LEN_HEADER = "content-length";
  public static final String CONTENT_TYPE_HEADER = "content-type";
  public static final String CONNECTION_HEADER = "connection";
  public static final String HTTP_1_1 = "HTTP/1.1";
  public static final String MDC_REQUEST_ID = "request_id";
  public static final String MDC_CONNECTION_ID = "connection_id";
  public static final String CACHE_CONTROL_HEADER = "cache-control";
  public static final String NO_CACHE_STORE = "no-store";

  public static final Set<HttpMethod> BODY_METHODS = Set.of(HttpMethod.POST, HttpMethod.PUT,
      HttpMethod.PATCH);
}
