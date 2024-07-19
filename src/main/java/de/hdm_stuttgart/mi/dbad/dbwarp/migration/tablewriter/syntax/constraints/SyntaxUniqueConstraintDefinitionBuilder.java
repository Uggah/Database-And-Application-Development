package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
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
public class SyntaxUniqueConstraintDefinitionBuilder implements
    ConstraintDefinitionBuilder<UniqueConstraint> {

  private final Syntax syntax;

  @Override
  public String createConstraintDefinitionStatement(final UniqueConstraint uniqueConstraint) {
    log.entry(uniqueConstraint);

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.COLUMN_NAMES,
        uniqueConstraint.getColumns().stream().map(Column::getName)
            .collect(Collectors.joining(", ")));

    final String out = StringSubstitutor.replace(
        SyntaxHelper.getInlineConstraintDefinitionByType(syntax, ConstraintType.UNIQUE).getValue(),
        params, PLACEHOLDER_BEGIN,
        PLACEHOLDER_END);

    return log.exit(out);
  }

}
