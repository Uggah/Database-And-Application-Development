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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.PostgreSQLProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PostgreSQLProvider.class)
class PostgreSQLTableReaderIntegrationTest {

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLTableReaderIntegrationTest.sql")
  void testReadTables(final ConnectionManager connectionManager) throws Exception {
    final PostgreSQLTableReader postgreSQLTableReader = new PostgreSQLTableReader(
        connectionManager);
    final List<Table> tables = postgreSQLTableReader.readTables();

    // SomeTestTable, SomeOtherTestTable are expected
    assertEquals(2, tables.size());

    final Table someTestTable = tables.stream()
        .filter(table -> table.getName().equalsIgnoreCase("SomeTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someTestTable.getType());
    assertEquals("public", someTestTable.getSchema());

    final Table someOtherTestTable = tables.stream()
        .filter(table -> table.getName().equalsIgnoreCase("SomeOtherTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someOtherTestTable.getType());
    assertEquals("public", someOtherTestTable.getSchema());

    postgreSQLTableReader.close();
  }

}
