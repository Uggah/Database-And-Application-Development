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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types.TypeConversionHelper;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

/**
 * SQLite specific implementation of a {@link ColumnReader}.
 */
@XSlf4j
public class SQLiteColumnReader extends AbstractColumnReader {

  private final PreparedStatement columnDefaultPreparedStatement;
  private final PreparedStatement isAutoIncrementPreparedStatement;
  private final PreparedStatement isPrimaryKeyPreparedStatement;

  public SQLiteColumnReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);
    this.columnDefaultPreparedStatement = this.connection.prepareStatement(
        """
              SELECT dflt_value AS default_value FROM pragma_table_info(?) WHERE name = ?;
            """);

    this.isAutoIncrementPreparedStatement = this.connection.prepareStatement(
        "SELECT * FROM sqlite_master WHERE tbl_name=? AND sql LIKE '%INTEGER PRIMARY KEY AUTOINCREMENT%';"
    );

    this.isPrimaryKeyPreparedStatement = this.connection.prepareStatement(
        "SELECT pk AS primary_key FROM pragma_table_info(?) WHERE name=?;"
    );
    log.exit();
  }

  /**
   * Reads a {@link Column} from the given {@link Table} and {@link ResultSet}. This method will
   * call {@link AbstractColumnReader#readColumn(Table, ResultSet)} and then additionally read the
   * default value of the column from the database. Also, it will adjust the read GenerationStrategy
   * to match the SQLite behavior.
   *
   * @param table     {@link Table} to read the {@link Column} from.
   * @param resultSet {@link ResultSet} containing information about the column. Is obtained using
   *                  {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)}.
   * @return
   * @throws SQLException
   */
  @Override
  protected Column readColumn(Table table, ResultSet resultSet) throws SQLException {
    log.entry(table, resultSet);
    final Column column = super.readColumn(table, resultSet);

    this.columnDefaultPreparedStatement.setString(1, table.getName());
    this.columnDefaultPreparedStatement.setString(2, column.getName());

    final ResultSet defaultValueResultSet = this.columnDefaultPreparedStatement.executeQuery();
    defaultValueResultSet.next();

    final Object defaultValue = defaultValueResultSet.getObject("default_value");

    if (defaultValue != null) {
      column.setDefaultValue(
          TypeConversionHelper.fromString(
              (String) defaultValue
          ).toType(column.getType())
      );

      if (column.getDefaultValue() instanceof String defaultValueString) {
        // The SQLite JDBC driver somehow returns strings with enclosing apostrophes. They are stripped in the next line.
        defaultValueString = defaultValueString.substring(1, defaultValueString.length() - 1);

        column.setDefaultValue(
            defaultValueString
        );
      }
    }

    this.isPrimaryKeyPreparedStatement.setString(1, table.getName());
    this.isPrimaryKeyPreparedStatement.setString(2, column.getName());

    final ResultSet primaryKeyResultSet = this.isPrimaryKeyPreparedStatement.executeQuery();
    primaryKeyResultSet.next();

    boolean isPrimaryKey = primaryKeyResultSet.getBoolean("primary_key");

    // For reference: https://www.sqlite.org/autoinc.html
    if (isPrimaryKey && column.getType() == JDBCType.INTEGER) {
      // Columns with type INTEGER that are part of the primary key are implicitly auto incrementing in SQLite.
      column.setGenerationStrategy(GenerationStrategy.SERIAL);

      this.isAutoIncrementPreparedStatement.setString(1, table.getName());
      final ResultSet autoIncrementResultSet = this.isAutoIncrementPreparedStatement.executeQuery();

      if (autoIncrementResultSet.next()) {
        // The AUTOINCREMENT keyword makes sure that the column's value is not only incrementing but also
        // unique among all rows that ever existed.
        column.setGenerationStrategy(GenerationStrategy.IDENTITY);
      }
    }

    return log.exit(column);
  }
}
