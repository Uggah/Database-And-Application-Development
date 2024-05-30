package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.PostgreSQLProvider;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PostgreSQLProvider.class)
class PostgreSQLColumnReaderIntegrationTest {

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLColumnReaderIntegrationTest.sql")
  void testReadColumns(final ConnectionManager connectionManager) throws Exception {
    final ColumnReader postgreSQLColumnReader = new PostgreSQLColumnReader(connectionManager);

    final Table table = new Table("public", "Person", TableType.TABLE);

    final List<Column> columns = postgreSQLColumnReader.readColumns(table);
    assertEquals(3, columns.size());

    columns.forEach(column -> assertSame(table, column.getTable()));

    postgreSQLColumnReader.close();
  }

}
