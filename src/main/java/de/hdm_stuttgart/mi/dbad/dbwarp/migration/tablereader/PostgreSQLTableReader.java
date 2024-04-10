package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;

public class PostgreSQLTableReader extends AbstractTableReader {

  public PostgreSQLTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
  }
}
