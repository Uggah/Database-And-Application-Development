package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
  public List<Constraint> readConstraints(Table table) throws SQLException {
    log.entry(table);

    final List<Constraint> constraints = new ArrayList<>();

    final PrimaryKeyConstraint primaryKeyConstraint = retrievePrimaryKeyConstraint(table);
    if (primaryKeyConstraint != null) {
      constraints.add(primaryKeyConstraint);
    }

    constraints.addAll(retrieveForeignKeyConstraints(table));
    constraints.addAll(retrieveUniqueConstraints(table));

    return log.exit(Collections.unmodifiableList(constraints));
  }

  protected abstract PrimaryKeyConstraint retrievePrimaryKeyConstraint(Table table)
      throws SQLException;

  protected abstract Collection<ForeignKeyConstraint> retrieveForeignKeyConstraints(Table table)
      throws SQLException;

  protected abstract Collection<UniqueConstraint> retrieveUniqueConstraints(Table table)
      throws SQLException;


}
