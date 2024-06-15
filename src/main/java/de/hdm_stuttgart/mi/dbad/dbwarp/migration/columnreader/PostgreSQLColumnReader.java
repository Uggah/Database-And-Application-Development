package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                   AND column_name=?
                   AND column_default IS NOT NULL;
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

    final Column column = new Column(
        table,
        name,
        JDBCType.valueOf(type),
        nullable,
        size
    );

    final ResultSet columnDefaultResultSet = columnDefaultPreparedStatement.executeQuery();

    if (columnDefaultResultSet.next()) {
      final String defaultValueUncasted = columnDefaultResultSet.getString("column_default");

      if (isPostgresFunction(defaultValueUncasted)) {
        return log.exit(column);
      }

      log.error(defaultValueUncasted);

      try (final Statement castStatement = this.connection.createStatement()) {
        final ResultSet castResultSet = castStatement.executeQuery(
            String.format("SELECT %s AS \"cast\";", defaultValueUncasted));
        castResultSet.next();

        final Object defaultValue = castResultSet.getObject("cast");

        column.setDefaultValue(defaultValue);
      }
    }

    return log.exit(column);
  }

  /**
   * Can be used to check if a given string represents a postgresql function. This is especially
   * useful with postgres specific syntax like 'DEFAULT gen_random_uuid()' which we do not want to
   * handle as a default but as a generated column. This method works by matching the given string
   * to a regex that matches all strings which look like a postgres function call.
   *
   * @param s string to match the regex against.
   * @return {@code true}, if the given string matches the regex. {@code false} if not.
   */
  private boolean isPostgresFunction(final String s) {
    final String regex = "[a-zA-Z_]+\\(.*\\)";

    return s.matches(regex);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    columnDefaultPreparedStatement.close();
    log.exit();
  }

}
