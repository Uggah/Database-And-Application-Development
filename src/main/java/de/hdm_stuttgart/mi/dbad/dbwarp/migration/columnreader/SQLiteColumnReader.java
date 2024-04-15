package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;

public class SQLiteColumnReader extends AbstractColumnReader {

  public SQLiteColumnReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
  }
}
