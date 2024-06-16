package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.JDBCType;
import java.util.List;
import org.junit.jupiter.api.Test;

class SQLiteTableDefinitionBuilderTest {

  @Test
  void testCreateTableDefinitionStatement() {
    final Table table = new Table("schema", "table", TableType.TABLE);
    table.addColumn(new Column(table, "column", JDBCType.INTEGER, true, 4));

    final SQLiteTableDefinitionBuilder builder = new SQLiteTableDefinitionBuilder();

    final String out = builder.createTableDefinitionStatement(table);

    assertEquals(
        "CREATE TABLE schema.table (column INTEGER(4));",
        out
    );
  }

  @Test
  void testCreateTableDefinitionStatement_NotNull() {
    final Table table = new Table("schema", "table", TableType.TABLE);
    table.addColumn(new Column(table, "column", JDBCType.INTEGER, false, 4));

    final SQLiteTableDefinitionBuilder builder = new SQLiteTableDefinitionBuilder();

    final String out = builder.createTableDefinitionStatement(table);

    assertEquals(
        "CREATE TABLE schema.table (column INTEGER(4) NOT NULL);",
        out
    );
  }

  @Test
  void testCreateTableDefinitionStatement_PrimaryKey() {
    final Table table = new Table("schema", "table", TableType.TABLE);

    final List<Column> columns = List.of(
        new Column(table, "column", JDBCType.INTEGER, false, 4)
    );

    table.addColumns(columns);

    table.addConstraint(new PrimaryKeyConstraint(table, columns));

    final SQLiteTableDefinitionBuilder builder = new SQLiteTableDefinitionBuilder();

    final String out = builder.createTableDefinitionStatement(table);

    assertEquals(
        "CREATE TABLE schema.table (column INTEGER(4) NOT NULL,PRIMARY KEY (column));",
        out
    );
  }

  @Test
  void testCreateTableDefinitionStatement_Unique() {
    final Table table = new Table("schema", "table", TableType.TABLE);

    final List<Column> columns = List.of(
        new Column(table, "column", JDBCType.INTEGER, false, 4),
        new Column(table, "column2", JDBCType.VARCHAR, true, 24)
    );

    table.addColumns(columns);

    final UniqueConstraint constraint = new UniqueConstraint(table, "");
    constraint.addColumn(columns.getFirst());
    table.addConstraint(constraint);

    final SQLiteTableDefinitionBuilder builder = new SQLiteTableDefinitionBuilder();

    final String out = builder.createTableDefinitionStatement(table);

    assertEquals(
        "CREATE TABLE schema.table (column INTEGER(4) NOT NULL,column2 VARCHAR(24),UNIQUE (column));",
        out
    );
  }

}