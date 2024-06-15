package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader.ColumnReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader.ConstraintReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

/**
 * Provides generic JDBC implementation of reading a schema. Inheriting classes must provide a DBMS
 * specific {@link TableReader}, {@link ColumnReader} and {@link ConstraintReader}.
 */
@XSlf4j
public abstract class AbstractJDBCSchemaReader implements SchemaReader {

  protected final Connection connection;
  protected final TableReader tableReader;
  protected final ColumnReader columnReader;
  protected final ConstraintReader constraintReader;

  protected AbstractJDBCSchemaReader(
      ConnectionManager connectionManager,
      TableReader tableReader,
      ColumnReader columnReader,
      ConstraintReader constraintReader
  ) {
    log.entry(connectionManager, tableReader, columnReader);
    this.connection = connectionManager.getSourceDatabaseConnection();
    this.tableReader = tableReader;
    this.columnReader = columnReader;
    this.constraintReader = constraintReader;
    log.exit();
  }

  @Override
  public List<Table> readSchema() throws SQLException {
    log.entry();

    final List<Table> tables = tableReader.readTables();

    for (final Table table : tables) {
      final List<Column> columns = columnReader.readColumns(table);
      table.addColumns(columns);

      final List<Constraint> constraints = constraintReader.readConstraints(
          table);
      table.addConstraints(constraints);
    }

    return log.exit(tables);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    tableReader.close();
    columnReader.close();
    constraintReader.close();
    log.exit();
  }

}
