package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SQLiteTableReaderIntegrationTest {

  private ConnectionManager connectionManager;
  private Connection connection;

  @BeforeEach
  void beforeEach() throws SQLException {
    this.connectionManager = mock(ConnectionManager.class);
    this.connection = DriverManager.getConnection(
        "jdbc:sqlite::resource:sqlite/SQLiteTableReaderIntegrationTest.db");

    when(connectionManager.getSourceDatabaseConnection()).thenReturn(connection);
  }

  @AfterEach
  void afterEach() throws SQLException {
    this.connection.close();
  }

  @Test
  void testReadTables() throws SQLException {
    final SQLiteTableReader sqLiteTableReader = new SQLiteTableReader(connectionManager);
    final List<Table> tables = sqLiteTableReader.readTables();

    // SomeTestTable, SomeOtherTestTable are expected
    assertEquals(2, tables.size());

    final Table someTestTable = tables.stream()
        .filter(table -> table.getDescriptor().getName().equals("SomeTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someTestTable.getDescriptor().getType());
    assertNull(someTestTable.getDescriptor().getSchema());

    final List<Column> someTestTableColumns = someTestTable.getColumns();

    assertEquals(2, someTestTableColumns.size());

    final Table someOtherTestTable = tables.stream()
        .filter(table -> table.getDescriptor().getName().equals("SomeOtherTestTable"))
        .findFirst()
        .orElseThrow();

    assertSame(TableType.TABLE, someOtherTestTable.getDescriptor().getType());
    assertNull(someOtherTestTable.getDescriptor().getSchema());

    final List<Column> someOtherTestTableColumns = someOtherTestTable.getColumns();

    assertEquals(1, someOtherTestTableColumns.size());

  }

}
