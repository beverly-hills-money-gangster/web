package com.demo.web.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON object mapper factory
 */
public interface ObjectMapperFactory {

  ObjectMapper create();
}
