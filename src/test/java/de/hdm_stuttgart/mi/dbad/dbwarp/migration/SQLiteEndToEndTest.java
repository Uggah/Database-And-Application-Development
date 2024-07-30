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
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.MariaDBProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.MockConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.PostgreSQLProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.ProvideDatabases;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.SQLiteProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConfigProvider.class)
@ExtendWith(EndToEndProvider.class)
class SQLiteEndToEndTest {

  @Test
  @ProvideDatabases(
      sourceProvider = SQLiteProvider.class,
      targetProvider = SQLiteProvider.class
  )
  @InitializeDatabase("sqlite/SQLiteEndToEndTest.sql")
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
    assertNotNull(ownerTable.getPrimaryKeyConstraint());
    assertEquals(1, ownerTable.getUniqueConstraints().size());

    final Table petTable = readTables.stream()
        .filter(table -> table.getName().equals("pet"))
        .findFirst()
        .orElseThrow();

    assertNull(petTable.getSchema());
    assertEquals(TableType.TABLE, petTable.getType());
    assertEquals(4, petTable.getColumns().size());
    assertNotNull(petTable.getPrimaryKeyConstraint());
    assertEquals(1, petTable.getForeignKeyConstraints().size());
  }

  @Test
  @ProvideDatabases(
      sourceProvider = SQLiteProvider.class,
      targetProvider = PostgreSQLProvider.class
  )
  @InitializeDatabase("sqlite/SQLiteEndToEndTest.sql")
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
    assertNotNull(ownerTable.getPrimaryKeyConstraint());
    assertEquals(1, ownerTable.getUniqueConstraints().size());

    final Table petTable = readTables.stream()
        .filter(table -> table.getName().equals("pet"))
        .findFirst()
        .orElseThrow();

    assertEquals("public", petTable.getSchema());
    assertEquals(TableType.TABLE, petTable.getType());
    assertEquals(4, petTable.getColumns().size());
    assertNotNull(petTable.getPrimaryKeyConstraint());
    assertEquals(1, petTable.getForeignKeyConstraints().size());
  }

  @Test
  @ProvideDatabases(
      sourceProvider = SQLiteProvider.class,
      targetProvider = MariaDBProvider.class
  )
  @InitializeDatabase("sqlite/SQLiteEndToEndTest.sql")
  void testToMariaDB_ReproducibleSchema(final ConnectionManager connectionManager)
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

    assertEquals("test", ownerTable.getSchema());
    assertEquals(TableType.TABLE, ownerTable.getType());
    assertEquals(4, ownerTable.getColumns().size());
    assertNotNull(ownerTable.getPrimaryKeyConstraint());
    assertEquals(1, ownerTable.getUniqueConstraints().size());

    final Table petTable = readTables.stream()
        .filter(table -> table.getName().equals("pet"))
        .findFirst()
        .orElseThrow();

    assertEquals("test", petTable.getSchema());
    assertEquals(TableType.TABLE, petTable.getType());
    assertEquals(4, petTable.getColumns().size());
    assertNotNull(petTable.getPrimaryKeyConstraint());
    assertEquals(1, petTable.getForeignKeyConstraints().size());
  }

  @Test
  @ProvideDatabases(
      sourceProvider = SQLiteProvider.class,
      targetProvider = PostgreSQLProvider.class
  )
  @InitializeDatabase("sqlite/SQLiteEndToEndTest_WithData.sql")
  void testToPostgreSQL_WithData(final ConnectionManager connectionManager)
      throws Exception {
    final MigrationManager migrationManager = MigrationManager.getInstance();
    migrationManager.setConnectionManager(connectionManager);
    migrationManager.migrate();

    final Connection targetConnection = connectionManager.getTargetDatabaseConnection();

    final Statement stmt = targetConnection.createStatement();

    final ResultSet ownerResultSet = stmt.executeQuery("SELECT * FROM public.owner");

    ownerResultSet.next();

    assertEquals(1, ownerResultSet.getInt("id"));
    assertEquals("Alice", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-01", ownerResultSet.getString("birth_date"));
    assertEquals("Wonderland", ownerResultSet.getString("address"));

    ownerResultSet.next();

    assertEquals(2, ownerResultSet.getInt("id"));
    assertEquals("Bob", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-02", ownerResultSet.getString("birth_date"));
    assertEquals("Bobsville", ownerResultSet.getString("address"));

    final ResultSet petResultSet = stmt.executeQuery("SELECT * FROM public.pet");

    petResultSet.next();

    assertEquals(1, petResultSet.getInt("id"));
    assertEquals("Fluffy", petResultSet.getString("name"));
    assertEquals("2010-01-01", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(2, petResultSet.getInt("id"));
    assertEquals("Spot", petResultSet.getString("name"));
    assertEquals("2010-01-02", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(3, petResultSet.getInt("id"));
    assertEquals("Rex", petResultSet.getString("name"));
    assertEquals("2010-01-03", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(4, petResultSet.getInt("id"));
    assertEquals("Fido", petResultSet.getString("name"));
    assertEquals("2010-01-04", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));
  }

  @Test
  @ProvideDatabases(
      sourceProvider = SQLiteProvider.class,
      targetProvider = SQLiteProvider.class
  )
  @InitializeDatabase("sqlite/SQLiteEndToEndTest_WithData.sql")
  void testToSQLite_WithData(final ConnectionManager connectionManager)
      throws Exception {
    final MigrationManager migrationManager = MigrationManager.getInstance();
    migrationManager.setConnectionManager(connectionManager);
    migrationManager.migrate();

    final Connection targetConnection = connectionManager.getTargetDatabaseConnection();

    final Statement stmt = targetConnection.createStatement();

    final ResultSet ownerResultSet = stmt.executeQuery("SELECT * FROM owner");

    ownerResultSet.next();

    assertEquals(1, ownerResultSet.getInt("id"));
    assertEquals("Alice", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-01", ownerResultSet.getString("birth_date"));
    assertEquals("Wonderland", ownerResultSet.getString("address"));

    ownerResultSet.next();

    assertEquals(2, ownerResultSet.getInt("id"));
    assertEquals("Bob", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-02", ownerResultSet.getString("birth_date"));
    assertEquals("Bobsville", ownerResultSet.getString("address"));

    final ResultSet petResultSet = stmt.executeQuery("SELECT * FROM pet");

    petResultSet.next();

    assertEquals(1, petResultSet.getInt("id"));
    assertEquals("Fluffy", petResultSet.getString("name"));
    assertEquals("2010-01-01", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(2, petResultSet.getInt("id"));
    assertEquals("Spot", petResultSet.getString("name"));
    assertEquals("2010-01-02", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(3, petResultSet.getInt("id"));
    assertEquals("Rex", petResultSet.getString("name"));
    assertEquals("2010-01-03", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(4, petResultSet.getInt("id"));
    assertEquals("Fido", petResultSet.getString("name"));
    assertEquals("2010-01-04", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));
  }

  @Test
  @ProvideDatabases(
      sourceProvider = SQLiteProvider.class,
      targetProvider = MariaDBProvider.class
  )
  @InitializeDatabase("sqlite/SQLiteEndToEndTest_WithData.sql")
  void testToMariaDB_WithData(final ConnectionManager connectionManager)
      throws Exception {
    final MigrationManager migrationManager = MigrationManager.getInstance();
    migrationManager.setConnectionManager(connectionManager);
    migrationManager.migrate();

    final Connection targetConnection = connectionManager.getTargetDatabaseConnection();

    final Statement stmt = targetConnection.createStatement();

    final ResultSet ownerResultSet = stmt.executeQuery("SELECT * FROM owner");

    ownerResultSet.next();

    assertEquals(1, ownerResultSet.getInt("id"));
    assertEquals("Alice", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-01", ownerResultSet.getString("birth_date"));
    assertEquals("Wonderland", ownerResultSet.getString("address"));

    ownerResultSet.next();

    assertEquals(2, ownerResultSet.getInt("id"));
    assertEquals("Bob", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-02", ownerResultSet.getString("birth_date"));
    assertEquals("Bobsville", ownerResultSet.getString("address"));

    final ResultSet petResultSet = stmt.executeQuery("SELECT * FROM pet");

    petResultSet.next();

    assertEquals(1, petResultSet.getInt("id"));
    assertEquals("Fluffy", petResultSet.getString("name"));
    assertEquals("2010-01-01", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(2, petResultSet.getInt("id"));
    assertEquals("Spot", petResultSet.getString("name"));
    assertEquals("2010-01-02", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(3, petResultSet.getInt("id"));
    assertEquals("Rex", petResultSet.getString("name"));
    assertEquals("2010-01-03", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(4, petResultSet.getInt("id"));
    assertEquals("Fido", petResultSet.getString("name"));
    assertEquals("2010-01-04", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));
  }

}
