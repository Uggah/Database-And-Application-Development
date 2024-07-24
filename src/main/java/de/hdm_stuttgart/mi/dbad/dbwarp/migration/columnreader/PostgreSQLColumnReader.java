package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

/*-
 * #%L
 * DBWarp
 * %%
 * Copyright (C) 2024 Kay Knöpfle, Lucca Greschner and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.XSlf4j;

/**
 * PostgreSQL specific implementation of a {@link ColumnReader}.
 */
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
    final Column column = super.readColumn(table, resultSet);

    if (column.getGenerationStrategy() == GenerationStrategy.SERIAL) {
      // PostgreSQL will always use a sequence for auto incrementing
      // Postgres' sequences will never emit the same value twice.
      // So, the IDENTITY strategy is the only strategy possible in PostgreSQL.
      column.setGenerationStrategy(GenerationStrategy.IDENTITY);
    }

    columnDefaultPreparedStatement.setString(1, table.getName());
    columnDefaultPreparedStatement.setString(2, column.getName());
    final ResultSet columnDefaultResultSet = columnDefaultPreparedStatement.executeQuery();

    if (columnDefaultResultSet.next()) {
      final String defaultValueUncasted = columnDefaultResultSet.getString("column_default");

      if (isPostgresFunction(defaultValueUncasted)) {
        return log.exit(column);
      }

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
