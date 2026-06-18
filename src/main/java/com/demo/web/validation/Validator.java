package com.demo.web.validation;

public interface Validator<T> {

  void validate(T t);
}
