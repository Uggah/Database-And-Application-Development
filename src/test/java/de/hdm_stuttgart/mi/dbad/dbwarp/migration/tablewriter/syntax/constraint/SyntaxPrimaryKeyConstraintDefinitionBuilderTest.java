package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraint;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.SyntaxPrimaryKeyConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SyntaxProvider.class)
@ExtendWith(ConfigProvider.class)
class SyntaxPrimaryKeyConstraintDefinitionBuilderTest {

  @Test
  @LoadSyntax("constraint_tests")
  void testCreateConstraintDefinitionStatement(final Syntax syntax) {
    final ConstraintDefinitionBuilder<PrimaryKeyConstraint> constraintDefinitionBuilder = new SyntaxPrimaryKeyConstraintDefinitionBuilder(
        syntax);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", null, false, 0);

    final PrimaryKeyConstraint primaryKeyConstraint = new PrimaryKeyConstraint("some_constraint",
        table);
    primaryKeyConstraint.getColumns().add(column);

    final String renderedStatement = constraintDefinitionBuilder.createConstraintDefinitionStatement(
        primaryKeyConstraint);
    assertEquals(
        "CONSTRAINT_NAME: some_constraint, TABLE_NAME: some_table, COLUMN_NAMES: some_column",
        renderedStatement
    );
  }

  @Test
  @LoadSyntax("constraint_tests")
  void testCreateConstraintDefinitionStatement_MultiColumn(final Syntax syntax) {
    final ConstraintDefinitionBuilder<PrimaryKeyConstraint> constraintDefinitionBuilder = new SyntaxPrimaryKeyConstraintDefinitionBuilder(
        syntax);

    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", null, false, 0);
    final Column column1 = new Column(table, "some_other_column", null, false, 0);

    final PrimaryKeyConstraint primaryKeyConstraint = new PrimaryKeyConstraint("some_constraint",
        table);
    primaryKeyConstraint.getColumns().add(column);
    primaryKeyConstraint.getColumns().add(column1);

    final String renderedStatement = constraintDefinitionBuilder.createConstraintDefinitionStatement(
        primaryKeyConstraint);
    assertEquals(
        "CONSTRAINT_NAME: some_constraint, TABLE_NAME: some_table, COLUMN_NAMES: some_column, some_other_column",
        renderedStatement
    );
  }

}
