package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.SQLException;
import java.util.List;

public interface SchemaReader extends AutoCloseable {

  List<Table> readSchema() throws SQLException;

}
