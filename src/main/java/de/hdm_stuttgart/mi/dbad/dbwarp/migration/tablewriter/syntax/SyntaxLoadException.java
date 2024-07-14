package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

public class SyntaxLoadException extends RuntimeException {

  public SyntaxLoadException(String message) {
    super(message);
  }

  public SyntaxLoadException(String message, Throwable cause) {
    super(message, cause);
  }

}
