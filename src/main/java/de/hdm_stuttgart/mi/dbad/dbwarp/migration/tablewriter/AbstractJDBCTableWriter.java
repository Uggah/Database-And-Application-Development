package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter;

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
import java.sql.Connection;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public abstract class AbstractJDBCTableWriter implements TableWriter {

  protected final Connection connection;

  protected AbstractJDBCTableWriter(final ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getTargetDatabaseConnection();
    log.exit(this);
  }

  @Override
  public void writeTable(final Table table) throws Exception {
    log.entry(table);
    this.writeTableDefinition(table);
    this.writeTableConstraints(table);
    log.exit();
  }

  public abstract void writeTableDefinition(final Table table) throws Exception;

  public abstract void writeTableConstraints(final Table table) throws Exception;
}
