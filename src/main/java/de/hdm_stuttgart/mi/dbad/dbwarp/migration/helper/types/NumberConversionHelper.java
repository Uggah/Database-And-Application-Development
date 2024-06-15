package de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NumberConversionHelper extends TypeConversionHelper {

  private final Number number;

  @Override
  public String toString() {
    return number.toString();
  }

  @Override
  public Integer toInteger() {
    return number.intValue();
  }

  @Override
  public Double toDouble() {
    return number.doubleValue();
  }

  @Override
  public Float toFloat() {
    return number.floatValue();
  }

  @Override
  public Boolean toBoolean() {
    return Double.compare(number.doubleValue(), 0d) > 0;
  }

}
