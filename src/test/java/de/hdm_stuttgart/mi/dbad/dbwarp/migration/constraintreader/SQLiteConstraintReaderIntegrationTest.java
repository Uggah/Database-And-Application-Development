package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.SQLiteProvider;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SQLiteProvider.class)
class SQLiteConstraintReaderIntegrationTest {

  @Test
  @InitializeDatabase("sqlite/SQLiteConstraintReaderIntegrationTest.sql")
  void testRetrieveUniqueConstraints(final ConnectionManager connectionManager) throws Exception {
    final SQLiteConstraintReader sqLiteConstraintReader = new SQLiteConstraintReader(
        connectionManager);

    final Table table = new Table(null, "Test", TableType.TABLE);
    table.addColumns(
        List.of(
            new Column(table, "id", JDBCType.INTEGER, false, 8),
            new Column(table, "unique_value", JDBCType.VARCHAR, false, 64)
        )
    );

    final List<UniqueConstraint> constraints = new ArrayList<>(
        sqLiteConstraintReader.retrieveUniqueConstraints(table));
    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("sqlite_autoindex_Test_1", constraint.getName());
    assertEquals(1, constraint.getColumns().size());
    assertSame(table.getColumns().getLast(), constraint.getColumns().getFirst());

    sqLiteConstraintReader.close();
  }


  /**
   * Tests the retrieval of a primary key constraint.
   *
   * @param connectionManager
   * @throws Exception
   */
  @Test
  @InitializeDatabase("sqlite/SQLiteConstraintReaderIntegrationTest.sql")
  void testRetriePrimaryKeyConstraint(final ConnectionManager connectionManager) throws Exception {
    final SQLiteConstraintReader sqLiteConstraintReader = new SQLiteConstraintReader(
        connectionManager);

    final Table table = new Table(null, "Test", TableType.TABLE);
    table.addColumns(
        List.of(
            new Column(table, "id", JDBCType.INTEGER, false, 8),
            new Column(table, "unique_value", JDBCType.VARCHAR, false, 64)
        )
    );

    final PrimaryKeyConstraint constraint = sqLiteConstraintReader.retrievePrimaryKeyConstraint(
        table);

    assertSame(table, constraint.getTable());
    assertEquals(1, constraint.getColumns().size());
    assertSame(table.getColumns().getFirst(), constraint.getColumns().getFirst());

    sqLiteConstraintReader.close();
  }

}
