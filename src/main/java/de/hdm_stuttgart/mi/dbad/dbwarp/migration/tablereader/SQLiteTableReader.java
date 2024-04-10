package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteTableReader extends DefaultTableReader {

  public SQLiteTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  @Override
  protected List<UniqueConstraint> retrieveUniqueConstraints(final Table table)
      throws SQLException {
    log.entry(table);

    final List<UniqueConstraint> constraints = new ArrayList<>();
    final ResultSet allIndexes = connection.createStatement()
        .executeQuery(String.format("PRAGMA index_list('%s')", table.getName()));
    final List<String> uniqueIndexes = new ArrayList<>();

    while (allIndexes.next()) {
      if (allIndexes.getBoolean("UNIQUE") && !allIndexes.getBoolean("PARTIAL")) {
        uniqueIndexes.add(allIndexes.getString("NAME"));
      }
    }

    for (String index : uniqueIndexes) {
      constraints.add(retriveUniqueConstraintByIndexName(index, table));
    }

    return constraints;
  }

  private UniqueConstraint retriveUniqueConstraintByIndexName(String indexName, Table table)
      throws SQLException {

    UniqueConstraint outConstraint = new UniqueConstraint();

    final ResultSet columns = connection.createStatement()
        .executeQuery(String.format("PRAGMA index_info('%s')", indexName));
    while (columns.next()) {
      outConstraint.addColumn(table.getColumnByName(columns.getString("NAME")));
    }

    return outConstraint;
  }

}
