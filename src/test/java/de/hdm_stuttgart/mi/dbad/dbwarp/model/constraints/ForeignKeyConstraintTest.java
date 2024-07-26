package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

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