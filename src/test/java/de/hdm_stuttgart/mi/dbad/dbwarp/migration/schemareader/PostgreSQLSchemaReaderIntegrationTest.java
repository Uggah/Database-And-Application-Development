package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

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

class PostgreSQLSchemaReaderIntegrationTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
      .withInitScript("postgreSQL/PostgreSQLTableReaderIntegrationTest.sql");
  private ConnectionManager connectionManager;
  private Connection connection;

  @BeforeAll
  static void beforeAll() {
    System.out.println(System.getenv());
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

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
    final PostgreSQLSchemaReader postgreSQLTableReader = new PostgreSQLSchemaReader(
        connectionManager);
    final List<Table> tables = postgreSQLTableReader.readSchema();

    // SomeTestTable, SomeOtherTestTable are expected
    assertEquals(2, tables.size());

    final Table someTestTable = tables.stream()
        .filter(table -> table.getName().equalsIgnoreCase("SomeTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someTestTable.getType());
    assertEquals("public", someTestTable.getSchema());

    final List<Column> someTestTableColumns = someTestTable.getColumns();

    assertEquals(2, someTestTableColumns.size());

    final Table someOtherTestTable = tables.stream()
        .filter(table -> table.getName().equalsIgnoreCase("SomeOtherTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someOtherTestTable.getType());
    assertEquals("public", someOtherTestTable.getSchema());

    final List<Column> someOtherTestTableColumns = someOtherTestTable.getColumns();

    assertEquals(1, someOtherTestTableColumns.size());
  }

}
