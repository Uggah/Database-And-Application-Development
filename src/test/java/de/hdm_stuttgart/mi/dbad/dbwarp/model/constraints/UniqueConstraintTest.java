package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import static org.junit.jupiter.api.Assertions.*;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.JDBCType;
import java.util.List;
import org.junit.jupiter.api.Test;

class UniqueConstraintTest {

  @Test
  void testGetName() {
    final UniqueConstraint uniqueConstraint = new UniqueConstraint("name", null, null);

    assertEquals("name", uniqueConstraint.getName());
  }

  @Test
  void testGetNameWithNullName() {
    final Table table = new Table(null, "table", TableType.TABLE);
    final List<Column> columnList = List.of(
        new Column(table, "column", JDBCType.INTEGER, false, 0)
    );

    final UniqueConstraint uniqueConstraint = new UniqueConstraint(null, table);

    uniqueConstraint.addColumns(columnList);

    assertEquals("UQ_table_on_column", uniqueConstraint.getName());
  }

  @Test
  void testGetNameWithNullName_MultipleColumns() {
    final Table childTable = new Table(null, "table", TableType.TABLE);
    final List<Column> columnList = List.of(
        new Column(childTable, "column", JDBCType.INTEGER, false, 0),
        new Column(childTable, "column2", JDBCType.CHAR, false, 0)
    );

    final UniqueConstraint uniqueConstraint = new UniqueConstraint(null, childTable);

    uniqueConstraint.addColumns(columnList);

    assertEquals("UQ_table_on_column_column2", uniqueConstraint.getName());
  }

}