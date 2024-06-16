package de.hdm_stuttgart.mi.dbad.dbwarp.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReaderFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.EndToEndProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.MockConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.ProvideDatabases;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.SQLiteProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
    assertEquals(3,
        ownerTable.getConstraints().size()); // TODO: Change this when issue #19 is fixed

    final Table petTable = readTables.stream()
        .filter(table -> table.getName().equals("pet"))
        .findFirst()
        .orElseThrow();

    assertNull(petTable.getSchema());
    assertEquals(TableType.TABLE, petTable.getType());
    assertEquals(4, petTable.getColumns().size());
    assertEquals(2, petTable.getConstraints()
        .size()); // TODO: Change this when issue #19 is fixed and PR #22 is merged
  }

}
