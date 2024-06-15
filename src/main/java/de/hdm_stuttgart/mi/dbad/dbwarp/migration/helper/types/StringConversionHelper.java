package de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types;

class StringConversionHelper extends TypeConversionHelper {

  private final String string;

  StringConversionHelper(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  public Integer toInteger() {
    return Integer.parseInt(string);
  }

  @Override
  public Double toDouble() {
    return Double.parseDouble(string);
  }

  @Override
  public Float toFloat() {
    return Float.parseFloat(string);
  }

  @Override
  public Boolean toBoolean() {
    return string != null;
  }

}
