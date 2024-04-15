package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PostgeSQLConstraintReader extends AbstractConstraintReader {

  public PostgeSQLConstraintReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
  }

  @Override
  public List<Constraint> readConstraints(Table table) throws SQLException {
    return Collections.emptyList();
  }
}
