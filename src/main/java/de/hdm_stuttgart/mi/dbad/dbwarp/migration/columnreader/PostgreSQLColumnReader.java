package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class PostgreSQLColumnReader extends AbstractColumnReader {

  private final PreparedStatement columnDefaultPreparedStatement;

  public PostgreSQLColumnReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);

    this.columnDefaultPreparedStatement = this.connection.prepareStatement(
        """
                   SELECT
                     column_default
                   FROM information_schema.columns
                   WHERE table_name=?
                   AND column_name=?;
            """
    );

    log.exit();
  }

  @Override
  protected Column readColumn(final Table table, final ResultSet resultSet) throws SQLException {
    log.entry(table, resultSet);

    final String name = resultSet.getString("COLUMN_NAME");
    final int type = resultSet.getInt("DATA_TYPE");

    final String nullability = resultSet.getString("IS_NULLABLE");
    final int size = resultSet.getInt("COLUMN_SIZE");

    final Boolean nullable;

    switch (nullability) {
      case "NO" -> nullable = false;
      case "YES" -> nullable = true;
      case null -> nullable = null;
      default -> throw new IllegalArgumentException(
          String.format("Unknown nullability: %s", nullability));
    }

    columnDefaultPreparedStatement.setString(1, table.getName());
    columnDefaultPreparedStatement.setString(2, name);

    final ResultSet columnDefaultResultSet = columnDefaultPreparedStatement.executeQuery();
    resultSet.next();

    final Column column = new Column(
        table,
        name,
        JDBCType.valueOf(type),
        nullable,
        size
    );

    column.setDefaultValue(columnDefaultResultSet.getObject("column_default"));

    return log.exit(column);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    columnDefaultPreparedStatement.close();
    log.exit();
  }

}
