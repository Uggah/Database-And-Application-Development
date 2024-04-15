package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractColumnReader implements ColumnReader {

  private final Connection connection;

  protected AbstractColumnReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  @Override
  public List<Column> readColumns(Table table) throws SQLException {
    log.entry(table);

    final ResultSet columns = connection.getMetaData().getColumns(null, table.getSchema(),
        table.getName(), "%");

    final List<Column> columnList = new ArrayList<>();

    while (columns.next()) {
      columnList.add(readColumn(columns));
    }

    return log.exit(Collections.unmodifiableList(columnList));
  }

  protected Column readColumn(final ResultSet resultSet) throws SQLException {
    final String name = resultSet.getString("COLUMN_NAME");
    final int type = resultSet.getInt("DATA_TYPE");

    final int nullability = resultSet.getInt("IS_NULLABLE");
    final int size = resultSet.getInt("COLUMN_SIZE");

    final Boolean nullable;

    switch (nullability) {
      case 0 -> nullable = false;
      case 1 -> nullable = true;
      case 2 -> nullable = null;
      default -> throw new IllegalArgumentException(
          String.format("Unknown nullability: %s", nullability));
    }

    return new Column(
        name,
        JDBCType.valueOf(type),
        nullable,
        size
    );
  }

}
