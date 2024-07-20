package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL primary key constraint definition statements based on a specific
 * syntax. This class is responsible for constructing the SQL statement for a primary key
 * constraint, according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxPrimaryKeyConstraintDefinitionBuilder implements
    ConstraintDefinitionBuilder<PrimaryKeyConstraint> {

  private final Syntax syntax;

  /**
   * Generates an SQL primary key constraint definition statement for a given primary key
   * constraint. This method takes into account the primary key constraint's properties and the
   * specific syntax rules, constructing a statement that defines the primary key constraint in
   * SQL.
   *
   * @param primaryKeyConstraint The primary key constraint for which to generate the definition
   *                             statement.
   * @return An SQL statement string that defines the primary key constraint.
   */
  @Override
  public String createConstraintDefinitionStatement(
      final PrimaryKeyConstraint primaryKeyConstraint) {
    log.entry(primaryKeyConstraint);

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.CONSTRAINT_NAME, primaryKeyConstraint.getName());

    params.put(SyntaxPlaceholders.TABLE_NAME, primaryKeyConstraint.getTable().getName());

    params.put(SyntaxPlaceholders.COLUMN_NAMES,
        primaryKeyConstraint.getColumns().stream().map(Column::getName)
            .collect(Collectors.joining(", ")));

    final String out = StringSubstitutor.replace(
        syntax.getTemplates().getPrimaryKeyConstraint().getValue(), params,
        PLACEHOLDER_BEGIN, PLACEHOLDER_END);

    return log.exit(out);
  }
}
