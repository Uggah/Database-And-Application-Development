package de.hdm_stuttgart.mi.dbad.dbwarp.migration;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReaderFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.EndToEndProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.MockConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.PostgreSQLProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.ProvideDatabases;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.SQLiteProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConfigProvider.class)
@ExtendWith(EndToEndProvider.class)
class PostgreSQLEndToEndTest {

  @Test
  @ProvideDatabases(
      sourceProvider = PostgreSQLProvider.class,
      targetProvider = SQLiteProvider.class
  )
  @InitializeDatabase("postgreSQL/PostgreSQLEndToEndTest.sql")
  void testToSQLite_ReproducibleSchema(final ConnectionManager connectionManager) throws Exception {
    final MigrationManager migrationManager = MigrationManager.getInstance();
    migrationManager.setConnectionManager(connectionManager);
    migrationManager.migrate();

    final ConnectionManager reversedConnectionManager = new MockConnectionManager(
        connectionManager.getTargetDatabaseConnection(), null);

    final SchemaReader schemaReader = new SchemaReaderFactory(
        reversedConnectionManager).getSchemaReader();
    final List<Table> readTables = schemaReader.readSchema();

    assertEquals(2, readTables.size());

    final Table ownerTable = readTables.stream()
        .filter(table -> table.getName().equals("owner"))
        .findFirst()
        .orElseThrow();

    assertNull(ownerTable.getSchema());
    assertEquals(TableType.TABLE, ownerTable.getType());
    assertEquals(4, ownerTable.getColumns().size());
    assertNotNull(
        ownerTable.getPrimaryKeyConstraint()); // TODO: Change this when more constraints are implemented

    final Table petTable = readTables.stream()
        .filter(table -> table.getName().equals("pet"))
        .findFirst()
        .orElseThrow();

    assertNull(petTable.getSchema());
    assertEquals(TableType.TABLE, petTable.getType());
    assertEquals(4, petTable.getColumns().size());
    assertNotNull(
        petTable.getPrimaryKeyConstraint()); // TODO: Change this when more constraints are implemented
  }

  @Test
  @ProvideDatabases(
      sourceProvider = PostgreSQLProvider.class,
      targetProvider = PostgreSQLProvider.class
  )
  @InitializeDatabase("postgreSQL/PostgreSQLEndToEndTest.sql")
  void testToPostgreSQL_ReproducibleSchema(final ConnectionManager connectionManager)
      throws Exception {
    final MigrationManager migrationManager = MigrationManager.getInstance();
    migrationManager.setConnectionManager(connectionManager);
    migrationManager.migrate();

    final ConnectionManager reversedConnectionManager = new MockConnectionManager(
        connectionManager.getTargetDatabaseConnection(), null);

    final SchemaReader schemaReader = new SchemaReaderFactory(
        reversedConnectionManager).getSchemaReader();
    final List<Table> readTables = schemaReader.readSchema();

    assertEquals(2, readTables.size());

    final Table ownerTable = readTables.stream()
        .filter(table -> table.getName().equals("owner"))
        .findFirst()
        .orElseThrow();

    assertEquals("public", ownerTable.getSchema());
    assertEquals(TableType.TABLE, ownerTable.getType());
    assertEquals(4, ownerTable.getColumns().size());
    assertNotNull(
        ownerTable.getPrimaryKeyConstraint()); // TODO: Change this when more constraints are implemented

    final Table petTable = readTables.stream()
        .filter(table -> table.getName().equals("pet"))
        .findFirst()
        .orElseThrow();

    assertEquals("public", petTable.getSchema());
    assertEquals(TableType.TABLE, petTable.getType());
    assertEquals(4, petTable.getColumns().size());
    assertNotNull(
        petTable.getPrimaryKeyConstraint()); // TODO: Change this when more constraints are implemented
  }

}
