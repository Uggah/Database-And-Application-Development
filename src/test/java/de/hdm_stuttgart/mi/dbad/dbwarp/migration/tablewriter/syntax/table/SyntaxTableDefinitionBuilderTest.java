package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.table;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.GenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.NotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import java.sql.JDBCType;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@XSlf4j
@ExtendWith(ConfigProvider.class)
@ExtendWith(SyntaxProvider.class)
class SyntaxTableDefinitionBuilderTest {

  private ColumnDefinitionBuilder columnDefinitionBuilder;
  private ConstraintDefinitionBuilder<PrimaryKeyConstraint> primaryKeyConstraintConstraintDefinitionBuilder;
  private ConstraintDefinitionBuilder<ForeignKeyConstraint> foreignKeyConstraintConstraintDefinitionBuilder;
  private ConstraintDefinitionBuilder<UniqueConstraint> uniqueConstraintConstraintDefinitionBuilder;
  private NotNullDefinitionBuilder notNullDefinitionBuilder;
  private GenerationStrategyDefinitionBuilder generationStrategyDefinitionBuilder;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void beforeEach() {
    this.columnDefinitionBuilder = mock(ColumnDefinitionBuilder.class);
    this.primaryKeyConstraintConstraintDefinitionBuilder = mock(ConstraintDefinitionBuilder.class);
    this.foreignKeyConstraintConstraintDefinitionBuilder = mock(ConstraintDefinitionBuilder.class);
    this.uniqueConstraintConstraintDefinitionBuilder = mock(ConstraintDefinitionBuilder.class);
    this.notNullDefinitionBuilder = mock(NotNullDefinitionBuilder.class);
    this.generationStrategyDefinitionBuilder = mock(GenerationStrategyDefinitionBuilder.class);
  }

  @Test
  @LoadSyntax("full_name")
  void testBuildTableDefinition_FullName(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, false, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, false, 255)
    ));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_schema.some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION );",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_line")
  void testBuildTableDefinition_NoBlockConstraints(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, false, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, false, 255)
    ));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals("CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION );",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_AllBlockConstraints_PrimaryKey(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(primaryKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(PrimaryKeyConstraint.class))).thenReturn("EXAMPLE_PRIMARY_KEY_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, true, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, true, 255)
    ));

    table.setPrimaryKeyConstraint(
        new PrimaryKeyConstraint("PK_first", table, List.of(table.getColumns().getFirst())));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_PRIMARY_KEY_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_AllBlockConstraints_ForeignKey(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(foreignKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(ForeignKeyConstraint.class))).thenReturn("EXAMPLE_FOREIGN_KEY_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, true, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, true, 255)
    ));

    table.addForeignKeyConstraint(new ForeignKeyConstraint("FK_first", table,
        new Table("some_schema", "some_other_table", TableType.TABLE)));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_FOREIGN_KEY_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_AllBlockConstraints_Unique(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(uniqueConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(UniqueConstraint.class))).thenReturn("EXAMPLE_UNIQUE_CONSTRAINT_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, true, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, true, 255)
    ));

    table.addUniqueConstraint(new UniqueConstraint("UQ_first", table));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_UNIQUE_CONSTRAINT_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_AllBlockConstraints_Combined(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(primaryKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(PrimaryKeyConstraint.class))).thenReturn("EXAMPLE_PRIMARY_KEY_DEFINITION");
    when(foreignKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(ForeignKeyConstraint.class))).thenReturn("EXAMPLE_FOREIGN_KEY_DEFINITION");
    when(uniqueConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
        any(UniqueConstraint.class))).thenReturn("EXAMPLE_UNIQUE_CONSTRAINT_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, false, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, false, 255)
    ));

    table.setPrimaryKeyConstraint(
        new PrimaryKeyConstraint("PK_first", table, List.of(table.getColumns().getFirst())));
    table.addForeignKeyConstraint(new ForeignKeyConstraint("FK_first", table,
        new Table("some_schema", "some_other_table", TableType.TABLE)));
    table.addUniqueConstraint(new UniqueConstraint("UQ_first", table));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertTrue(renderedDefinition.matches(
            "CREATE TABLE some_table \\(EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, .*\\);"),
        "Rendered definition: " + renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_NotNull(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(notNullDefinitionBuilder.createNotNullDefinitionStatement(
        any(Column.class))).thenReturn("EXAMPLE_NOT_NULL_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, false, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, true, 255)
    ));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_NOT_NULL_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_NotNull_MultipleColumns(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(notNullDefinitionBuilder.createNotNullDefinitionStatement(
        any(Column.class))).thenReturn("EXAMPLE_NOT_NULL_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, false, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, false, 255)
    ));

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_NOT_NULL_DEFINITION, EXAMPLE_NOT_NULL_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_Generation_Serial(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(generationStrategyDefinitionBuilder.createGenerationStrategyDefinitionStatement(
        any(Column.class))).thenReturn("EXAMPLE_GENERATION_STRATEGY_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, true, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, true, 255)
    ));

    table.getColumns().getFirst().setGenerationStrategy(GenerationStrategy.SERIAL);

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_GENERATION_STRATEGY_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

  @Test
  @LoadSyntax("end_of_block")
  void testBuildTableDefinition_Generation_Identity(final Syntax syntax) {
    final SyntaxTableDefinitionBuilder syntaxTableDefinitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        primaryKeyConstraintConstraintDefinitionBuilder,
        foreignKeyConstraintConstraintDefinitionBuilder,
        uniqueConstraintConstraintDefinitionBuilder,
        notNullDefinitionBuilder,
        generationStrategyDefinitionBuilder
    );

    when(columnDefinitionBuilder.createColumnDefinitionStatement(any())).thenReturn(
        "EXAMPLE_COLUMN_DEFINITION");
    when(generationStrategyDefinitionBuilder.createGenerationStrategyDefinitionStatement(
        any(Column.class))).thenReturn("EXAMPLE_GENERATION_STRATEGY_DEFINITION");

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    table.addColumns(List.of(
        new Column(table, "some_column", JDBCType.VARCHAR, true, 255),
        new Column(table, "some_other_column", JDBCType.INTEGER, true, 255)
    ));

    table.getColumns().getFirst().setGenerationStrategy(GenerationStrategy.IDENTITY);

    final String renderedDefinition = syntaxTableDefinitionBuilder.createTableDefinitionStatement(
        table);

    assertEquals(
        "CREATE TABLE some_table (EXAMPLE_COLUMN_DEFINITION, EXAMPLE_COLUMN_DEFINITION, EXAMPLE_GENERATION_STRATEGY_DEFINITION);",
        renderedDefinition);

    table.getColumns()
        .forEach(column -> verify(columnDefinitionBuilder).createColumnDefinitionStatement(column));
  }

}
