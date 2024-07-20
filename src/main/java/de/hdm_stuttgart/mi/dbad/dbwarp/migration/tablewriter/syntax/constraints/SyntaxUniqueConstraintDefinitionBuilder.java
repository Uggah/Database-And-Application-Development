package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL unique constraint definition statements based on a specific
 * syntax. This class is responsible for constructing the SQL statement for a unique constraint,
 * according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxUniqueConstraintDefinitionBuilder implements
    ConstraintDefinitionBuilder<UniqueConstraint> {

  private final Syntax syntax;

  /**
   * Generates an SQL unique constraint definition statement for a given unique constraint. This
   * method takes into account the unique constraint's properties and the specific syntax rules,
   * constructing a statement that defines the unique constraint in SQL.
   *
   * @param uniqueConstraint The unique constraint for which to generate the definition statement.
   * @return An SQL statement string that defines the unique constraint.
   */
  @Override
  public String createConstraintDefinitionStatement(final UniqueConstraint uniqueConstraint) {
    log.entry(uniqueConstraint);

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.CONSTRAINT_NAME, uniqueConstraint.getName());

    params.put(SyntaxPlaceholders.TABLE_NAME, uniqueConstraint.getTable().getName());

    params.put(SyntaxPlaceholders.COLUMN_NAMES,
        uniqueConstraint.getColumns().stream().map(Column::getName)
            .collect(Collectors.joining(", ")));

    final String out = StringSubstitutor.replace(
        syntax.getTemplates().getUniqueConstraint().getValue(),
        params,
        PLACEHOLDER_BEGIN,
        PLACEHOLDER_END
    );

    return log.exit(out);
  }

}
