package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

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
    final PostgreSQLConstraintReader postgreSQLConstraintReader = new PostgreSQLConstraintReader(
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
        postgreSQLConstraintReader.retrieveUniqueConstraints(table)
    );

    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("test_table1_something_unique_key", constraint.getName());
    assertEquals(1, constraint.getColumns().size());
    assertSame(table.getColumns().getLast(), constraint.getColumns().getFirst());

    postgreSQLConstraintReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLConstraintReaderIntegrationTest_UniqueConstraints.sql")
  void testRetrieveUniqueConstraints_MultipleColumns(final ConnectionManager connectionManager)
      throws Exception {
    final PostgreSQLConstraintReader postgreSQLConstraintReader = new PostgreSQLConstraintReader(
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
        postgreSQLConstraintReader.retrieveUniqueConstraints(table)
    );

    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("test_table2_something_unique_something_else_key", constraint.getName());
    assertEquals(2, constraint.getColumns().size());
    assertTrue(constraint.getColumns().contains(table.getColumns().get(1)));
    assertTrue(constraint.getColumns().contains(table.getColumns().get(2)));

    postgreSQLConstraintReader.close();
  }

  @Test
  @InitializeDatabase("postgreSQL/PostgreSQLConstraintReaderIntegrationTest_UniqueConstraints.sql")
  void testRetrieveUniqueConstraints_CustomConstraintName(final ConnectionManager connectionManager)
      throws Exception {
    final PostgreSQLConstraintReader postgreSQLConstraintReader = new PostgreSQLConstraintReader(
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
        postgreSQLConstraintReader.retrieveUniqueConstraints(table)
    );

    assertEquals(1, constraints.size());

    final UniqueConstraint constraint = constraints.getFirst();

    assertSame(table, constraint.getTable());
    assertEquals("custom_constraint_name", constraint.getName());
    assertEquals(1, constraint.getColumns().size());
    assertTrue(constraint.getColumns().contains(table.getColumns().getLast()));

    postgreSQLConstraintReader.close();
  }

}
