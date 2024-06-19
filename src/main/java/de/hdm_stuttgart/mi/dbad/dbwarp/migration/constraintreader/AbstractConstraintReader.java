package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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

  @Override
  public void readConstraints(List<Table> tableList) throws SQLException {
    log.entry(tableList);

    retrievePrimaryKeyConstraint(tableList);
    retrieveUniqueConstraints(tableList);
    retrieveForeignKeyConstraints(tableList);

  }

  protected abstract void retrievePrimaryKeyConstraint(List<Table> tableList)
      throws SQLException;

  protected abstract void retrieveForeignKeyConstraints(
      List<Table> tableList)
      throws SQLException;

  protected abstract void retrieveUniqueConstraints(List<Table> tableList)
      throws SQLException;

}
