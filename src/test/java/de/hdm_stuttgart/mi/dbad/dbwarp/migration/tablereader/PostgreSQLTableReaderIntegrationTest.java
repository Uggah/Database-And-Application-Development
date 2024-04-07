package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class PostgreSQLTableReaderIntegrationTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
      .withInitScript("postgreSQL/PostgreSQLTableReaderIntegrationTest.sql");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  private ConnectionManager connectionManager;
  private Connection connection;

  @BeforeEach
  void beforeEach() throws SQLException {
    this.connectionManager = mock(ConnectionManager.class);
    this.connection = DriverManager.getConnection(
        postgres.getJdbcUrl(),
        postgres.getUsername(),
        postgres.getPassword()
    );

    when(connectionManager.getSourceDatabaseConnection()).thenReturn(connection);
  }

  @AfterEach
  void afterEach() throws SQLException {
    this.connection.close();
  }

  @Test
  void testReadTables() throws SQLException {
    final PostgreSQLTableReader postgreSQLTableReader = new PostgreSQLTableReader(
        connectionManager);
    final List<Table> tables = postgreSQLTableReader.readTables();

    // SomeTestTable, SomeOtherTestTable are expected
    assertEquals(2, tables.size());

    final Table someTestTable = tables.stream()
        .filter(table -> table.getDescriptor().getName().equalsIgnoreCase("SomeTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someTestTable.getDescriptor().getType());
    assertEquals("public", someTestTable.getDescriptor().getSchema());

    final List<Column> someTestTableColumns = someTestTable.getColumns();

    assertEquals(2, someTestTableColumns.size());

    final Table someOtherTestTable = tables.stream()
        .filter(table -> table.getDescriptor().getName().equalsIgnoreCase("SomeOtherTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someOtherTestTable.getDescriptor().getType());
    assertEquals("public", someOtherTestTable.getDescriptor().getSchema());

    final List<Column> someOtherTestTableColumns = someOtherTestTable.getColumns();

    assertEquals(1, someOtherTestTableColumns.size());
  }

}
