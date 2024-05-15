package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

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
