package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
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
  public List<Constraint> readConstraints(Table table, List<Table> tableList) throws SQLException {
    log.entry(table);
    return log.exit(Collections.emptyList());
  }

  @Override
  protected PrimaryKeyConstraint retrievePrimaryKeyConstraint(Table table) throws SQLException {
    log.entry();
    return log.exit(null);
  }

  @Override
  protected Collection<ForeignKeyConstraint> retrieveForeignKeyConstraints(Table table,
      List<Table> tableList)
      throws SQLException {
    log.entry();
    return log.exit(Collections.emptyList());
  }

  @Override
  protected Collection<UniqueConstraint> retrieveUniqueConstraints(Table table)
      throws SQLException {
    log.entry();
    return log.exit(Collections.emptyList());
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }
}
