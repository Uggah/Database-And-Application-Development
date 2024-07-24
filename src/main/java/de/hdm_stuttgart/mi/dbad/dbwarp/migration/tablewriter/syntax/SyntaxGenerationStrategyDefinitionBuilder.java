package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.GenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

@XSlf4j
@RequiredArgsConstructor
public class SyntaxGenerationStrategyDefinitionBuilder implements
    GenerationStrategyDefinitionBuilder {

  private final Syntax syntax;

  /**
   * @param column
   * @return
   */
  public String createGenerationStrategyDefinitionStatement(final Column column) {
    log.entry(column);
    final Map<String, String> params = new HashMap<>();

    params.put(
        SyntaxPlaceholders.SCHEMA_NAME,
        Objects.requireNonNullElse(column.getTable().getSchema(), syntax.getDefaultSchema())
    );
    params.put(SyntaxPlaceholders.TABLE_NAME, column.getTable().getName());
    params.put(SyntaxPlaceholders.COLUMN_NAME, column.getName());

    return log.exit(StringSubstitutor.replace(
        SyntaxHelper.getGenerationTemplate(syntax, column.getGenerationStrategy()).getValue(),
        params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END
    ));
  }

}
