package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.PostgreSQLProvider;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PostgreSQLProvider.class)
class PostgreSQLConstraintReaderIntegrationTest {

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLConstraintReaderIntegrationTest_UniqueConstraints.sql")
  void testRetrieveUniqueConstraints_SingleColumn(final ConnectionManager connectionManager)
      throws Exception {
    final PostgeSQLConstraintReader postgeSQLConstraintReader = new PostgeSQLConstraintReader(
        connectionManager
    );

    final Table table = new Table(null, "test_table1", TableType.TABLE);
    table.addColumns(
        List.of(
            new Column(table, "id", JDBCType.INTEGER, false, 8),
            new Column(table, "something_unique", JDBCType.VARCHAR, true, 10)
        )
    );

    final List<UniqueConstraint> constraints = new ArrayList<>(
        postgeSQLConstraintReader.retrieveUniqueConstraints(table)
    );

    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("test_table1_something_unique_key", constraint.getName());
    assertEquals(1, constraint.getColumns().size());
    assertSame(table.getColumns().getLast(), constraint.getColumns().getFirst());

    postgeSQLConstraintReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLConstraintReaderIntegrationTest_UniqueConstraints.sql")
  void testRetrieveUniqueConstraints_MultipleColumns(final ConnectionManager connectionManager)
      throws Exception {
    final PostgeSQLConstraintReader postgeSQLConstraintReader = new PostgeSQLConstraintReader(
        connectionManager
    );

    final Table table = new Table(null, "test_table2", TableType.TABLE);
    table.addColumns(
        List.of(
            new Column(table, "id", JDBCType.INTEGER, false, 8),
            new Column(table, "something_unique", JDBCType.VARCHAR, true, 10),
            new Column(table, "something_else", JDBCType.BOOLEAN, true, 1)
        )
    );

    final List<UniqueConstraint> constraints = new ArrayList<>(
        postgeSQLConstraintReader.retrieveUniqueConstraints(table)
    );

    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("test_table2_something_unique_something_else_key", constraint.getName());
    assertEquals(2, constraint.getColumns().size());
    assertTrue(constraint.getColumns().contains(table.getColumns().get(1)));
    assertTrue(constraint.getColumns().contains(table.getColumns().get(2)));

    postgeSQLConstraintReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLConstraintReaderIntegrationTest_UniqueConstraints.sql")
  void testRetrieveUniqueConstraints_CustomConstraintName(final ConnectionManager connectionManager)
      throws Exception {
    final PostgeSQLConstraintReader postgeSQLConstraintReader = new PostgeSQLConstraintReader(
        connectionManager
    );

    final Table table = new Table(null, "test_table3", TableType.TABLE);
    table.addColumns(
        List.of(
            new Column(table, "id", JDBCType.INTEGER, false, 8),
            new Column(table, "something_unique", JDBCType.VARCHAR, true, 10)
        )
    );

    final List<UniqueConstraint> constraints = new ArrayList<>(
        postgeSQLConstraintReader.retrieveUniqueConstraints(table)
    );

    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("custom_constraint_name", constraint.getName());
    assertEquals(1, constraint.getColumns().size());
    assertTrue(constraint.getColumns().contains(table.getColumns().getLast()));

    postgeSQLConstraintReader.close();
  }

}
