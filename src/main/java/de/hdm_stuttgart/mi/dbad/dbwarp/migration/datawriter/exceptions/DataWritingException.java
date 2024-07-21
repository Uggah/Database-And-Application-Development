package de.hdm_stuttgart.mi.dbad.dbwarp.migration.datawriter.exceptions;

public class DataWritingException extends RuntimeException {

  public DataWritingException(final String message) {
    super(message);
  }

  public DataWritingException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
