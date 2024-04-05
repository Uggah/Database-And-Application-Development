package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Table;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteTableReader extends TableReader {

  public SQLiteTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
  }

  @Override
  public List<Table> readTables() throws SQLException {

    Map<String, Table> outTables = new HashMap<>();

    Connection connection = connectionManager.getSourceDatabaseConnection();
    ResultSet columns = connection.getMetaData().getColumns(null, null, "%", "%");
    while (columns.next()) {
      String table_name = columns.getString("TABLE_NAME");
      if (!outTables.containsKey(table_name)) {
        outTables.put(table_name, new Table(table_name));
      }
      outTables.get(table_name).addColumn(new Column(columns.getString("COLUMN_NAME"),
          JDBCType.valueOf(columns.getInt("DATA_TYPE"))));
    }

    return new ArrayList<>(outTables.values());
  }
}
