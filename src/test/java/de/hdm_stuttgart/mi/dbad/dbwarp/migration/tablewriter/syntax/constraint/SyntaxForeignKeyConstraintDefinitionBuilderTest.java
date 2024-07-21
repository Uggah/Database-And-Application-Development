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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.SyntaxForeignKeyConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SyntaxProvider.class)
class SyntaxForeignKeyConstraintDefinitionBuilderTest {

  @Test
  @LoadSyntax("constraint_tests")
  void testCreateConstraintDefinitionStatement(final Syntax syntax) {
    final ConstraintDefinitionBuilder<ForeignKeyConstraint> constraintDefinitionBuilder = new SyntaxForeignKeyConstraintDefinitionBuilder(
        syntax);

    final Table childTable = new Table("some_schema", "some_table", TableType.TABLE);
    final Column referencingColumn = new Column(childTable, "some_column", null, false, 0);
    childTable.addColumn(referencingColumn);

    final Table parentTable = new Table("some_schema", "some_referenced_table", TableType.TABLE);
    final Column referencedColumn = new Column(parentTable, "some_referenced_column", null, false,
        0);
    parentTable.addColumn(referencedColumn);

    final ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint("some_constraint",
        childTable, parentTable);
    foreignKeyConstraint.getChildColumns().add(referencingColumn);
    foreignKeyConstraint.getParentColumns().add(referencedColumn);

    final String renderedStatement = constraintDefinitionBuilder.createConstraintDefinitionStatement(
        foreignKeyConstraint);

    assertEquals(
        "CONSTRAINT_NAME:some_constraint,COLUMN_NAMES:some_column,CHILD_TABLE_NAME:some_table,CHILD_COLUMN_NAMES:some_column,PARENT_TABLE_NAME:some_referenced_table,PARENT_COLUMN_NAMES:some_referenced_column",
        StringUtils.deleteWhitespace(renderedStatement)
    );
  }

  @Test
  @LoadSyntax("constraint_tests")
  void testCreateConstraintDefinitionStatement_MultiColumns(final Syntax syntax) {
    final ConstraintDefinitionBuilder<ForeignKeyConstraint> constraintDefinitionBuilder = new SyntaxForeignKeyConstraintDefinitionBuilder(
        syntax);

    final Table childTable = new Table("some_schema", "some_table", TableType.TABLE);
    final Column referencingColumn = new Column(childTable, "some_column", null, false, 0);
    final Column referencingColumn2 = new Column(childTable, "some_other_column", null, false, 0);
    childTable.addColumns(List.of(referencingColumn, referencingColumn2));

    final Table parentTable = new Table("some_schema", "some_referenced_table", TableType.TABLE);
    final Column referencedColumn = new Column(parentTable, "some_referenced_column", null, false,
        0);
    final Column referencedColumn2 = new Column(parentTable, "some_other_referenced_column", null,
        false, 0);
    parentTable.addColumns(List.of(referencedColumn, referencedColumn2));

    final ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint("some_constraint",
        childTable, parentTable);
    foreignKeyConstraint.getChildColumns().addAll(childTable.getColumns());
    foreignKeyConstraint.getParentColumns().addAll(parentTable.getColumns());

    final String renderedStatement = constraintDefinitionBuilder.createConstraintDefinitionStatement(
        foreignKeyConstraint);

    assertEquals(
        "CONSTRAINT_NAME:some_constraint,COLUMN_NAMES:some_column,some_other_column,CHILD_TABLE_NAME:some_table,CHILD_COLUMN_NAMES:some_column,some_other_column,PARENT_TABLE_NAME:some_referenced_table,PARENT_COLUMN_NAMES:some_referenced_column,some_other_referenced_column",
        StringUtils.deleteWhitespace(renderedStatement)
    );
  }

}
