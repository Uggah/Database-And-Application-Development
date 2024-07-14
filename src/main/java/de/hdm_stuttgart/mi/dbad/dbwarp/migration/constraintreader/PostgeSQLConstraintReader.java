package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class PostgeSQLConstraintReader extends AbstractConstraintReader implements AutoCloseable {

  public PostgeSQLConstraintReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  @Override
  public void readConstraints(List<Table> tableList) throws SQLException {
    log.entry(tableList);
    log.exit();
  }

  @Override
  protected void retrievePrimaryKeyConstraint(List<Table> tableList) throws SQLException {
    log.entry();
    log.exit();
  }

  @Override
  protected void retrieveForeignKeyConstraints(List<Table> tableList)
      throws SQLException {
    log.entry();
    log.exit();
  }

  @Override
  protected void retrieveUniqueConstraints(List<Table> tableList)
      throws SQLException {
    log.entry();
    log.exit();
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }
}
