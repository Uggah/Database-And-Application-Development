package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Table;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public abstract class TableReader {

  protected final ConnectionManager connectionManager;

  protected TableReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connectionManager = connectionManager;
    log.exit();
  }

  public abstract List<Table> readTables() throws SQLException;

}
