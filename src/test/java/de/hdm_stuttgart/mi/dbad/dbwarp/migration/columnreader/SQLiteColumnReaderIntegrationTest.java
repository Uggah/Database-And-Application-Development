package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SQLiteColumnReaderIntegrationTest {

  private ConnectionManager connectionManager;
  private Connection connection;

  @BeforeEach
  void beforeEach() throws Exception {

    Files.copy(Paths.get("src/test/resources/sqlite/SQLiteSchemaReaderIntegrationTest.db"),
        Paths.get("src/test/resources/sqlite/SQLiteSchemaReaderIntegrationTest.temp.db"));

    this.connectionManager = mock(ConnectionManager.class);
    this.connection = DriverManager.getConnection(
        "jdbc:sqlite:src/test/resources/sqlite/SQLiteSchemaReaderIntegrationTest.temp.db");

    when(connectionManager.getSourceDatabaseConnection()).thenReturn(connection);
  }

  @AfterEach
  void afterEach() throws Exception {
    this.connection.close();
    Files.deleteIfExists(
        Paths.get("src/test/resources/sqlite/SQLiteSchemaReaderIntegrationTest.temp.db"));
  }

  @Test
  void testReadColumns() throws Exception {

    Table testTable = new Table(null, "SomeTestTable", TableType.TABLE);
    testTable.addColumn(new Column(testTable, "id", JDBCType.INTEGER, false, 2000000000));
    testTable.addColumn(new Column(testTable, "test", JDBCType.VARCHAR, false, 24));

    final SQLiteColumnReader columnReader = new SQLiteColumnReader(connectionManager);
    List<Column> columnList = columnReader.readColumns(testTable);

    Assertions.assertIterableEquals(testTable.getColumns(), columnList);


  }

}