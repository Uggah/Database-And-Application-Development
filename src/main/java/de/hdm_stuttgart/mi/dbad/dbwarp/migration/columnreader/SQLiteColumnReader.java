package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteColumnReader extends AbstractColumnReader {

  public SQLiteColumnReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }
}
