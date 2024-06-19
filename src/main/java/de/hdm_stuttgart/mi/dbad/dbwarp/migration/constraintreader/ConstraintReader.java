package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.SQLException;
import java.util.List;

public interface ConstraintReader extends AutoCloseable {

  void readConstraints(List<Table> tableList) throws SQLException;
}
