package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.SyntaxPrimaryKeyConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SyntaxProvider.class)
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
