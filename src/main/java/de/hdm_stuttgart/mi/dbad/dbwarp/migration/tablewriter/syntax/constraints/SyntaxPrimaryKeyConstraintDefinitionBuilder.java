package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintType;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

@XSlf4j
@RequiredArgsConstructor
public class SyntaxPrimaryKeyConstraintDefinitionBuilder implements
    ConstraintDefinitionBuilder<PrimaryKeyConstraint> {

  private final Syntax syntax;

  @Override
  public String createConstraintDefinitionStatement(
      final PrimaryKeyConstraint primaryKeyConstraint) {
    log.entry(primaryKeyConstraint);

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.COLUMN_NAMES,
        primaryKeyConstraint.getColumns().stream().map(Column::getName)
            .collect(Collectors.joining(", ")));

    final String out = StringSubstitutor.replace(
        SyntaxHelper.getInlineConstraintDefinitionByType(syntax, ConstraintType.PRIMARY_KEY)
            .getValue(), params,
        PLACEHOLDER_BEGIN, PLACEHOLDER_END);

    return log.exit(out);
  }
}
