package com.demo.web.json;

import java.util.Arrays;
import java.util.Objects;
import lombok.Data;

@Data
public class JsonSamplePojo {

  private String text;
  private int number;
  private boolean flag;
  private String[] array;

  public static JsonSamplePojo createDummy() {
    JsonSamplePojo samplePojo = new JsonSamplePojo();
    samplePojo.setFlag(true);
    samplePojo.setText("Test");
    samplePojo.setNumber(67);
    samplePojo.setArray(
        new String[]{
            "Hello, World!",
            "¡Hola, Mundo!",
            "Bonjour, le monde !",
            "Hallo, Welt!",
            "Ciao, Mondo!",
            "Olá, Mundo!",
            "Привет, мир!",
            "你好，世界！",
            "こんにちは、世界！",
            "مرحبًا بالعالم"});
    return samplePojo;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    JsonSamplePojo pojo = (JsonSamplePojo) object;
    return number == pojo.number && flag == pojo.flag && Objects.equals(text, pojo.text)
        && Objects.deepEquals(array, pojo.array);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, number, flag, Arrays.hashCode(array));
  }
}