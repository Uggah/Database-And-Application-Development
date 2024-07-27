package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.JDBCType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ForeignKeyConstraintTest {

  @Test
  void testGetName() {
    final ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint("name", null, null);

    assertEquals("name", foreignKeyConstraint.getName());
  }

  @Test
  void testGetNameWithNullName() {
    final Table childTable = new Table(null, "child", TableType.TABLE);
    final List<Column> columnList = List.of(
        new Column(childTable, "column", JDBCType.INTEGER, false, 0)
    );

    final Table parentTable = new Table(null, "parent", TableType.TABLE);

    final ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint(null, childTable,
        parentTable);

    foreignKeyConstraint.getChildColumns().add(columnList.getFirst());

    assertEquals("FK_child_parent_on_column", foreignKeyConstraint.getName());
  }

  @Test
  void testGetNameWithNullName_MultipleColumns() {
    final Table childTable = new Table(null, "child", TableType.TABLE);
    final List<Column> columnList = List.of(
        new Column(childTable, "column", JDBCType.INTEGER, false, 0),
        new Column(childTable, "column2", JDBCType.CHAR, false, 0)
    );

    final Table parentTable = new Table(null, "parent", TableType.TABLE);

    final ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint(null, childTable,
        parentTable);

    foreignKeyConstraint.getChildColumns().addAll(columnList);

    assertEquals("FK_child_parent_on_column_column2", foreignKeyConstraint.getName());
  }

}
