package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.PostgreSQLProvider;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PostgreSQLProvider.class)
class PostgreSQLColumnReaderIntegrationTest {

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLColumnReaderIntegrationTest.sql")
  void testReadColumns(final ConnectionManager connectionManager) throws Exception {
    final ColumnReader postgreSQLColumnReader = new PostgreSQLColumnReader(connectionManager);

    final Table table = new Table("public", "person", TableType.TABLE);

    final List<Column> columns = postgreSQLColumnReader.readColumns(table);
    assertEquals(3, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    postgreSQLColumnReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLColumnReaderIntegrationTest_DefaultValue.sql")
  void testReadColumns_DefaultValue(final ConnectionManager connectionManager) throws Exception {
    final ColumnReader postgreSQLColumnReader = new PostgreSQLColumnReader(connectionManager);

    final Table table = new Table("public", "person", TableType.TABLE);

    final List<Column> columns = postgreSQLColumnReader.readColumns(table);
    assertEquals(2, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    final Column idColumn = columns.getFirst();
    final Column nameColumn = columns.getLast();

    assertNull(idColumn.getDefaultValue());
    assertEquals("John Doe", nameColumn.getDefaultValue());

    postgreSQLColumnReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLColumnReaderIntegrationTest_DefaultValue.sql")
  void testReadColumns_DefaultValueUUID(final ConnectionManager connectionManager)
      throws Exception {
    final ColumnReader postgreSQLColumnReader = new PostgreSQLColumnReader(connectionManager);

    final Table table = new Table("public", "uuiddefaultvalue", TableType.TABLE);

    final List<Column> columns = postgreSQLColumnReader.readColumns(table);
    assertEquals(2, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    final Column idColumn = columns.getFirst();
    final Column nameColumn = columns.getLast();

    assertNull(idColumn.getDefaultValue());
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"),
        nameColumn.getDefaultValue());

    postgreSQLColumnReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLColumnReaderIntegrationTest_DefaultValue.sql")
  void testReadColumns_DefaultValueGeneratedUUID(final ConnectionManager connectionManager)
      throws Exception {
    final ColumnReader postgreSQLColumnReader = new PostgreSQLColumnReader(connectionManager);

    final Table table = new Table("public", "generateddefault", TableType.TABLE);

    final List<Column> columns = postgreSQLColumnReader.readColumns(table);
    assertEquals(2, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    final Column idColumn = columns.getFirst();
    final Column nameColumn = columns.getLast();

    assertNull(idColumn.getDefaultValue());
    assertNull(nameColumn.getDefaultValue());

    postgreSQLColumnReader.close();
  }

}
