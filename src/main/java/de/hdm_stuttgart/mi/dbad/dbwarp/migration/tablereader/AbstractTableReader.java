package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * Implements a generic approach to reading tables from the source database. It needs to be extended
 * by a DBMS specific {@link TableReader} that adjusts to implementation details.
 */
@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractTableReader implements TableReader {

  private final Connection connection;

  protected AbstractTableReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  /**
   * Retrieves all non-system tables {@link Table}, that are neither temporary nor views.
   *
   * @return an unmodifiable {@link List} of all retrieved {@link Table Tables}
   * @throws SQLException if a database access error occurs
   */
  @Override
  public List<Table> readTables() throws SQLException {
    log.entry();

    final ResultSet tables = connection.getMetaData()
        .getTables(null, null, "%", new String[]{"TABLE"});

    final List<Table> outTables = new ArrayList<>();

    while (tables.next()) {
      final Table outTable = new Table(
          tables.getString("TABLE_SCHEM"),
          tables.getString("TABLE_NAME"),
          TableType.byTableTypeString(tables.getString("TABLE_TYPE"))
      );

      outTables.add(outTable);
    }

    return log.exit(Collections.unmodifiableList(outTables));
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }

}
