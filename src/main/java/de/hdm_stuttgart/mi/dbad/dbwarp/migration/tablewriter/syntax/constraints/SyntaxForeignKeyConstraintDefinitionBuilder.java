package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL foreign key constraint definition statements based on a specific
 * syntax. This class is responsible for constructing the SQL statement for a foreign key constraint
 * definition, according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxForeignKeyConstraintDefinitionBuilder implements
    ConstraintDefinitionBuilder<ForeignKeyConstraint> {

  private final Syntax syntax;

  /**
   * Generates a SQL foreign key constraint definition statement for a given foreign key
   * constraint.
   *
   * @param foreignKeyConstraint The constraint
   * @return The constraint definition statement
   */
  @Override
  public String createConstraintDefinitionStatement(
      final ForeignKeyConstraint foreignKeyConstraint) {
    log.entry(foreignKeyConstraint);

    final Map<String, String> params = new HashMap<>();

    params.put(
        SyntaxPlaceholders.CONSTRAINT_NAME,
        foreignKeyConstraint.getName()
    );

    final String childColumnNames = foreignKeyConstraint.getChildColumns().stream()
        .map(Column::getName)
        .collect(Collectors.joining(", "));

    params.put(
        SyntaxPlaceholders.COLUMN_NAMES,
        childColumnNames
    );

    params.put(
        SyntaxPlaceholders.CHILD_TABLE_SCHEMA_NAME,
        foreignKeyConstraint.getChildTable().getSchema()
    );

    params.put(
        SyntaxPlaceholders.CHILD_TABLE_NAME,
        foreignKeyConstraint.getChildTable().getName()
    );

    params.put(
        SyntaxPlaceholders.CHILD_COLUMN_NAMES,
        childColumnNames
    );

    params.put(
        SyntaxPlaceholders.PARENT_TABLE_SCHEMA_NAME,
        foreignKeyConstraint.getParentTable().getSchema()
    );

    params.put(
        SyntaxPlaceholders.PARENT_TABLE_NAME,
        foreignKeyConstraint.getParentTable().getName()
    );

    params.put(
        SyntaxPlaceholders.PARENT_COLUMN_NAMES,
        foreignKeyConstraint.getParentColumns().stream()
            .map(Column::getName)
            .collect(Collectors.joining(", "))
    );

    final String out = StringSubstitutor.replace(
        syntax.getTemplates().getForeignKeyConstraint().getValue(),
        params,
        PLACEHOLDER_BEGIN,
        PLACEHOLDER_END
    );

    return log.exit(out);
  }

}
