package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.SQLiteProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SQLiteProvider.class)
class SQLiteColumnReaderIntegrationTest {

  @Test
  @InitializeDatabase("sqlite/SQLiteColumnReaderIntegrationTest.sql")
  void testReadColumns(final ConnectionManager connectionManager) throws Exception {
    final ColumnReader sqLiteColumnReader = new SQLiteColumnReader(connectionManager);

    final Table table = new Table(null, "Person", TableType.TABLE);

    final List<Column> columns = sqLiteColumnReader.readColumns(table);
    assertEquals(3, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    sqLiteColumnReader.close();
  }

  @Test
  @InitializeDatabase("sqlite/SQLiteColumnReaderIntegrationTest.sql")
  void testReadColumns_DefaultValueInt(final ConnectionManager connectionManager) throws Exception {
    final ColumnReader sqLiteColumnReader = new SQLiteColumnReader(connectionManager);

    final Table table = new Table(null, "IntDefaultValue", TableType.TABLE);

    final List<Column> columns = sqLiteColumnReader.readColumns(table);
    assertEquals(2, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    columns.stream().filter(column -> column.getName().equals("with_default_value"))
        .forEach(column -> {
          assertEquals(1, column.getDefaultValue());
        });

    sqLiteColumnReader.close();
  }

  @Test
  @InitializeDatabase("sqlite/SQLiteColumnReaderIntegrationTest.sql")
  void testReadColumns_RealDefaultValue(final ConnectionManager connectionManager)
      throws Exception {
    final ColumnReader sqLiteColumnReader = new SQLiteColumnReader(connectionManager);

    final Table table = new Table(null, "RealDefaultValue", TableType.TABLE);

    final List<Column> columns = sqLiteColumnReader.readColumns(table);
    assertEquals(2, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    columns.stream().filter(column -> column.getName().equals("with_default_value"))
        .forEach(column -> {
          assertEquals(1.0f, column.getDefaultValue());
        });

    sqLiteColumnReader.close();
  }

  @Test
  @InitializeDatabase("sqlite/SQLiteColumnReaderIntegrationTest.sql")
  void testReadColumns_TextDefaultValue(final ConnectionManager connectionManager)
      throws Exception {
    final ColumnReader sqLiteColumnReader = new SQLiteColumnReader(connectionManager);

    final Table table = new Table(null, "TextDefaultValue", TableType.TABLE);

    final List<Column> columns = sqLiteColumnReader.readColumns(table);
    assertEquals(2, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    columns.stream().filter(column -> column.getName().equals("with_default_value"))
        .forEach(column -> {
          assertEquals("SomeDefaultValue", column.getDefaultValue());
        });

    sqLiteColumnReader.close();
  }

}
