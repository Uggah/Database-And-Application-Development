package de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types;

import java.sql.JDBCType;

public abstract class TypeConversionHelper {

  public abstract String toString();

  public abstract Integer toInteger();

  public abstract Double toDouble();

  public abstract Float toFloat();

  public abstract Boolean toBoolean();

  public static TypeConversionHelper fromString(String string) {
    return new StringConversionHelper(string);
  }

  public static TypeConversionHelper fromNumber(Number number) {
    return new NumberConversionHelper(number);
  }

  public static TypeConversionHelper fromInteger(Integer integer) {
    return fromNumber(integer);
  }

  public static TypeConversionHelper fromDouble(Double d) {
    return fromNumber(d);
  }

  public static TypeConversionHelper fromFloat(Float f) {
    return fromNumber(f);
  }

  public static TypeConversionHelper fromBoolean(Boolean bool) {
    return new BooleanConversionHelper(bool);
  }

  public static TypeConversionHelper fromType(JDBCType type, Object object) {
    switch (type) {
      case TINYINT, SMALLINT, INTEGER, FLOAT, DOUBLE, NUMERIC, REAL, DECIMAL -> {
        return fromNumber((Number) object);
      }
      case CHAR, VARCHAR, LONGVARCHAR -> {
        return fromString((String) object);
      }
      case BOOLEAN, BIT -> {
        return fromBoolean((Boolean) object);
      }
      default -> throw new IllegalArgumentException(
          "JDBC type conversion for given type is not implemented yet!");
    }
  }

  public Object toType(JDBCType type) {
    switch (type) {
      case TINYINT, SMALLINT, INTEGER -> {
        return toInteger();
      }
      case FLOAT, REAL -> {
        return toFloat();
      }
      case DOUBLE, NUMERIC, DECIMAL -> {
        return toDouble();
      }
      case CHAR, VARCHAR, LONGVARCHAR -> {
        return toString();
      }
      case BOOLEAN, BIT -> {
        return toBoolean();
      }
      default -> throw new IllegalArgumentException(
          "JDBC type conversion for given type is not implemented yet!");
    }
  }

}
