package de.hdm_stuttgart.mi.dbad.dbwarp.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class ConnectionManager {

  private final Connection sourceDatabaseConnection;

  private final Connection targetDatabaseConnection;

  @SneakyThrows
  public ConnectionManager(final String source, final String target) {
    this.sourceDatabaseConnection = DriverManager.getConnection(source);
    this.targetDatabaseConnection = DriverManager.getConnection(target);
  }

}
