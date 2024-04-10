package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;

public class SQLiteTableReader extends AbstractTableReader {

  public SQLiteTableReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
  }
}
