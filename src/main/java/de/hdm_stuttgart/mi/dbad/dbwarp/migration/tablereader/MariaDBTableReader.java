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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

/**
 * Implementation of a {@link TableReader} for MariaDB. Customized for MariaDB because MariaDB maps
 * Database to JDBC Catalog.
 */
@XSlf4j
public class MariaDBTableReader extends AbstractTableReader {

  public MariaDBTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  /**
   * Reads all tables from the database.
   *
   * @return A list of all tables in the database.
   * @throws SQLException If an error occurs while reading the tables.
   */
  @Override
  public List<Table> readTables() throws SQLException {
    log.entry();

    final ResultSet tables = connection.getMetaData()
        .getTables(null, null, "%", new String[]{"TABLE"});

    final List<Table> outTables = new ArrayList<>();

    while (tables.next()) {
      final Table outTable = new Table(
          tables.getString("TABLE_CAT"),
          tables.getString("TABLE_NAME"),
          TableType.byTableTypeString(tables.getString("TABLE_TYPE"))
      );

      outTables.add(outTable);
    }

    return log.exit(Collections.unmodifiableList(outTables));
  }

}
