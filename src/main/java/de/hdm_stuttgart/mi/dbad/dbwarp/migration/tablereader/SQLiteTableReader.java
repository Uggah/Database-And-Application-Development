package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteTableReader extends DefaultTableReader {

  public SQLiteTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

}
