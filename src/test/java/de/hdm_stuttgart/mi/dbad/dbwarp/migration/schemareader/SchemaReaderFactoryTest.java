package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

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
