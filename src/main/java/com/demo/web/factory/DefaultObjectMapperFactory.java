package com.demo.web.factory;

import com.demo.annotation.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Returns default JSON object mapper implementation
 */
@Component
public class DefaultObjectMapperFactory implements ObjectMapperFactory {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public ObjectMapper create() {
    return OBJECT_MAPPER;
  }
}
