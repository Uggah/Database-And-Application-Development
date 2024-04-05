package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Table;
import java.sql.SQLException;
import java.util.List;

public abstract class TableReader {

  protected final ConnectionManager connectionManager;

  protected TableReader(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  public abstract List<Table> readTables() throws SQLException;

}
