package de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BooleanConversionHelper extends TypeConversionHelper {

  private final Boolean bool;

  @Override
  public String toString() {
    return Boolean.toString(bool);
  }

  @Override
  public Integer toInteger() {
    return Boolean.TRUE.equals(bool) ? 1 : 0;
  }

  @Override
  public Double toDouble() {
    return Boolean.TRUE.equals(bool) ? 1d : 0d;
  }

  @Override
  public Float toFloat() {
    return Boolean.TRUE.equals(bool) ? 1f : 0f;
  }

  @Override
  public Boolean toBoolean() {
    return bool;
  }
}
