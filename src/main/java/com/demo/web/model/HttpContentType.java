package com.demo.web.model;

import com.demo.web.util.Constants;
import java.util.Arrays;
import lombok.Getter;
import lombok.NonNull;


public enum HttpContentType {
  // Text
  TEXT("text/plain"),
  HTML("text/html"),
  CSS("text/css"),
  CSV("text/csv"),
  JAVASCRIPT("text/javascript"),
  XML("application/xml"),
  JSON("application/json"),
  YAML("application/x-yaml"),

  // Images
  PNG("image/png"),
  JPEG("image/jpeg"),
  GIF("image/gif"),
  BMP("image/bmp"),
  WEBP("image/webp"),
  SVG("image/svg+xml"),
  TIFF("image/tiff"),
  ICO("image/x-icon"),

  // Audio
  MP3("audio/mpeg"),
  WAV("audio/wav"),
  OGG_AUDIO("audio/ogg"),
  AAC("audio/aac"),
  FLAC("audio/flac"),

  // Video
  MP4("video/mp4"),
  MPEG_VIDEO("video/mpeg"),
  WEBM("video/webm"),
  OGG_VIDEO("video/ogg"),
  AVI("video/x-msvideo"),
  QUICKTIME("video/quicktime"),

  // Archives
  ZIP("application/zip"),
  GZIP("application/gzip"),
  TAR("application/x-tar"),
  RAR("application/vnd.rar"),
  SEVEN_ZIP("application/x-7z-compressed"),

  // Documents
  PDF("application/pdf"),
  DOC("application/msword"),
  DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
  XLS("application/vnd.ms-excel"),
  XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  PPT("application/vnd.ms-powerpoint"),
  PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

  // Fonts
  TTF("font/ttf"),
  OTF("font/otf"),
  WOFF("font/woff"),
  WOFF2("font/woff2"),

  // Binary
  OCTET_STREAM("application/octet-stream"),

  // Forms
  FORM_URLENCODED("application/x-www-form-urlencoded"),
  FORM_DATA("multipart/form-data"),

  // Other
  PDF_FORM("application/fdf"),
  RSS("application/rss+xml"),
  ATOM("application/atom+xml");


  HttpContentType(String type) {
    this.type = type + "; charset=" + Constants.DEFAULT_CHARSET.name();
  }

  public static HttpContentType get(final @NonNull String type) {
    return Arrays.stream(HttpContentType.values())
        .filter(contentType -> contentType.type.startsWith(type.trim()))
        .findFirst().orElseThrow(
            () -> new IllegalArgumentException("Can't find content type %s".formatted(type)));
  }

  @Getter
  private final String type;
}
