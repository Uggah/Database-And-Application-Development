package de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.exception;

public class DriverLoadingException extends RuntimeException {

  public DriverLoadingException(String message, Exception reason) {
    super(message, reason);
  }

  public DriverLoadingException(String message) {
    super(message);
  }

  public DriverLoadingException(Exception reason) {
    super(String.format("Exception occurred while loading driver: %s", reason.getMessage()),
        reason);
  }

}
