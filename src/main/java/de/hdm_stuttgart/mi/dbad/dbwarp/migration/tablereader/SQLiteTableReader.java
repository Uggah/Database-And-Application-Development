package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Table;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteTableReader extends TableReader {

  public SQLiteTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  @Override
  public List<Table> readTables() throws SQLException {
    log.entry();

    final Map<String, Table> outTables = new HashMap<>();
    final Connection connection = connectionManager.getSourceDatabaseConnection();

    final ResultSet columns = connection.getMetaData().getColumns(null, null, "%", "%");

    while (columns.next()) {
      final String tableName = columns.getString("TABLE_NAME");

      final Table table = outTables.computeIfAbsent(tableName, Table::new);
      table.addColumn(
          new Column(
              columns.getString("COLUMN_NAME"),
              JDBCType.valueOf(columns.getInt("DATA_TYPE"))
          ));
    }

    return log.exit(Collections.unmodifiableList(new ArrayList<>(outTables.values())));
  }
}
