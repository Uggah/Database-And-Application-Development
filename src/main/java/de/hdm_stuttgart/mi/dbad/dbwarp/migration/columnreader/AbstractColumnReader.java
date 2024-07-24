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
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * Generic JDBC implementation of a {@link ColumnReader}. Will work in most cases. However, JDBC
 * drivers that do not strictly follow the specifications (like PostgreSQL) will need a specific
 * implementation.
 */
@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractColumnReader implements ColumnReader {

  protected final Connection connection;

  protected AbstractColumnReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  /**
   * Reads all {@link Column} definitions from the given {@link Table}.
   *
   * @param table {@link Table} to read the {@link Column} definitions from.
   * @return a {@link List} of {@link Column Columns}.
   * @throws SQLException if an SQL error occurs while reading {@link Column Columns}.
   */
  @Override
  public List<Column> readColumns(Table table) throws SQLException {
    log.entry(table);

    final ResultSet columns = connection.getMetaData().getColumns(
        null,
        table.getSchema(),
        table.getName(),
        "%"
    );

    final List<Column> columnList = new ArrayList<>();

    while (columns.next()) {
      columnList.add(readColumn(table, columns));
    }

    return log.exit(Collections.unmodifiableList(columnList));
  }

  /**
   * Reads a {@link Column} from the given {@link Table} using the given {@link ResultSet}.
   *
   * @param table     {@link Table} to read the {@link Column} from.
   * @param resultSet {@link ResultSet} containing information about the column. Is obtained using
   *                  {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)}.
   * @return the read {@link Column}.
   * @throws SQLException if an SQL error occurs while reading {@link Column} information.
   */
  protected Column readColumn(final Table table, final ResultSet resultSet) throws SQLException {
    log.entry(table, resultSet);

    final String name = resultSet.getString("COLUMN_NAME");
    final int type = resultSet.getInt("DATA_TYPE");

    final String isNullable = resultSet.getString("IS_NULLABLE");
    final int size = resultSet.getInt("COLUMN_SIZE");

    final String isAutoincrement = resultSet.getString("IS_AUTOINCREMENT");

    final Boolean nullable;

    switch (isNullable) {
      case "NO" -> nullable = false;
      case "YES" -> nullable = true;
      case "" -> nullable = null;
      default -> throw new IllegalArgumentException(
          String.format("Unknown nullability: %s", isNullable));
    }

    final Column column = new Column(
        table,
        name,
        JDBCType.valueOf(type),
        nullable,
        size
    );

    if (isAutoincrement.equals("YES")) {
      // If auto increment is returned as yes, the column has either GenerationStrategy SERIAL or IDENTITY.
      // Support for GenerationStrategy IDENTITY is vendor specific and must therefore be handled
      // in the specific implementation.
      column.setGenerationStrategy(GenerationStrategy.SERIAL);
    }

    return log.exit(column);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }

}
