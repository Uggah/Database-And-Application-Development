package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.DefaultConnectionManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemaReaderFactoryTest {

  private DefaultConnectionManager connectionManager;
  private DatabaseMetaData databaseMetaData;

  @BeforeEach
  void beforeEach() throws SQLException {
    this.connectionManager = mock(DefaultConnectionManager.class);
    this.databaseMetaData = mock(DatabaseMetaData.class);

    final Connection mockedConnection = mock(Connection.class);
    when(mockedConnection.getMetaData()).thenReturn(this.databaseMetaData);

    when(connectionManager.getSourceDatabaseConnection()).thenReturn(mockedConnection);
  }

  @Test
  void testGetTableReader_SQLite() throws SQLException {
    when(this.databaseMetaData.getDatabaseProductName()).thenReturn("SQLite");

    final SchemaReaderFactory factory = new SchemaReaderFactory(connectionManager);
    final SchemaReader schemaReader = factory.getSchemaReader();

    assertInstanceOf(SQLiteSchemaReader.class, schemaReader);
  }

  @Test
  void testGetTableReader_UnsupportedDatabase() throws SQLException {
    when(this.databaseMetaData.getDatabaseProductName()).thenReturn("UNSUPPORTED");

    final SchemaReaderFactory factory = new SchemaReaderFactory(connectionManager);

    assertThrows(IllegalArgumentException.class, factory::getSchemaReader);
  }

}
