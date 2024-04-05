package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel.Table;
import java.sql.SQLException;

public abstract class TableReader {

  private ConnectionManager connectionManager;

  public TableReader(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  ;

  public abstract Table[] readTables() throws SQLException;

}
