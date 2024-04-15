package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.Connection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractConstraintReader implements ConstraintReader {

  protected final Connection connection;

  protected AbstractConstraintReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

}
