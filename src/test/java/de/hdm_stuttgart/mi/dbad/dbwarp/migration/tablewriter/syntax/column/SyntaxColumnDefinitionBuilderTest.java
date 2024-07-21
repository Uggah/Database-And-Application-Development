package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import java.sql.JDBCType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SyntaxProvider.class)
class SyntaxColumnDefinitionBuilderTest {

  private ConstraintDefinitionBuilder<Constraint> constraintDefinitionBuilder;

  @BeforeEach
  void beforeEach() {
    this.constraintDefinitionBuilder = mock(ConstraintDefinitionBuilder.class);
  }

  @Test
  @LoadSyntax("end_of_line")
  void testCreateColumnDefinitionStatement(final Syntax syntax) {
    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax, this.constraintDefinitionBuilder);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", JDBCType.VARCHAR, true, 255);

    final String renderedDefinition = columnDefinitionBuilder.createColumnDefinitionStatement(
        column);

    assertEquals("some_column VARCHAR  ", renderedDefinition);
  }

  @Test
  @LoadSyntax("end_of_line")
  void testCreateColumnDefinitionStatement_NotNull(final Syntax syntax) {
    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax, this.constraintDefinitionBuilder);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", JDBCType.VARCHAR, false, 255);

    final String renderedDefinition = columnDefinitionBuilder.createColumnDefinitionStatement(
        column);

    assertEquals("some_column VARCHAR  EXAMPLE_NOT_NULL_CONSTRAINT", renderedDefinition);
  }

  @Test
  @LoadSyntax("end_of_line")
  void testCreateColumnDefinitionStatement_PrimaryKey(final Syntax syntax) {
    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax, this.constraintDefinitionBuilder);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", JDBCType.VARCHAR, true, 255);

    table.setPrimaryKeyConstraint(new PrimaryKeyConstraint("PK_first", table, List.of(column)));

    when(this.constraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(PrimaryKeyConstraint.class))).thenReturn("EXAMPLE_PRIMARY_KEY_CONSTRAINT");

    final String renderedDefinition = columnDefinitionBuilder.createColumnDefinitionStatement(
        column);

    assertEquals("some_column VARCHAR  EXAMPLE_PRIMARY_KEY_CONSTRAINT", renderedDefinition);
  }

  @Test
  @LoadSyntax("end_of_line")
  void testCreateColumnDefinitionStatement_ForeignKey(final Syntax syntax) {
    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax, this.constraintDefinitionBuilder);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", JDBCType.VARCHAR, true, 255);

    final ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint("FK_first", table,
        new Table("some_schema", "some_table", TableType.TABLE));
    foreignKeyConstraint.getChildColumns().add(column);

    table.addForeignKeyConstraint(foreignKeyConstraint);

    when(this.constraintDefinitionBuilder.createConstraintDefinitionStatement(any(
        ForeignKeyConstraint.class))).thenReturn("EXAMPLE_FOREIGN_KEY_CONSTRAINT");

    final String renderedDefinition = columnDefinitionBuilder.createColumnDefinitionStatement(
        column);

    assertEquals("some_column VARCHAR  EXAMPLE_FOREIGN_KEY_CONSTRAINT", renderedDefinition);
  }

  @Test
  @LoadSyntax("end_of_line")
  void testCreateColumnDefinitionStatement_Unique(final Syntax syntax) {
    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax, this.constraintDefinitionBuilder);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", JDBCType.VARCHAR, true, 255);

    final UniqueConstraint uniqueConstraint = new UniqueConstraint("UQ_First", table);
    uniqueConstraint.addColumn(column);
    table.addUniqueConstraint(uniqueConstraint);

    when(this.constraintDefinitionBuilder.createConstraintDefinitionStatement(any(
        UniqueConstraint.class))).thenReturn("EXAMPLE_UNIQUE_CONSTRAINT");

    final String renderedDefinition = columnDefinitionBuilder.createColumnDefinitionStatement(
        column);

    assertEquals("some_column VARCHAR  EXAMPLE_UNIQUE_CONSTRAINT", renderedDefinition);
  }

  @Test
  @LoadSyntax("end_of_line")
  void testCreateColumnDefinitionStatement_DefaultValue(final Syntax syntax) {
    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax, this.constraintDefinitionBuilder);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", JDBCType.VARCHAR, true, 255);
    column.setDefaultValue("some default value");

    final String renderedDefinition = columnDefinitionBuilder.createColumnDefinitionStatement(
        column);

    assertEquals("some_column VARCHAR DEFAULT 'some default value' ", renderedDefinition);
  }


}
