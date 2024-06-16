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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.SQLiteTableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Statement;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteTableWriter extends AbstractJDBCTableWriter {

  private final SQLiteTableDefinitionBuilder definitionBuilder = new SQLiteTableDefinitionBuilder();

  protected SQLiteTableWriter(final ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit(this);
  }

  @Override
  public void writeTableDefinition(final Table table) throws Exception {
    log.entry(table);
    final String definition = definitionBuilder.createTableDefinitionStatement(table);

    try (final Statement stmt = this.connection.createStatement()) {
      stmt.execute(definition);
    }

    log.exit();
  }

  @Override
  public void writeTableConstraints(Table ignored) throws Exception {
    // Empty implementation as this step has to be handled in the table definition
  }
}
