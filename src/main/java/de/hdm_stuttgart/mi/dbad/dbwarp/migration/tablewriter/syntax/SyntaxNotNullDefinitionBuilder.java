package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.NotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

@RequiredArgsConstructor
public class SyntaxNotNullDefinitionBuilder implements NotNullDefinitionBuilder {

  private final Syntax syntax;

  @Override
  public String createNotNullDefinitionStatement(Column column) {
    final Map<String, String> params = new HashMap<>();

    params.put(
        SyntaxPlaceholders.SCHEMA_NAME,
        Objects.requireNonNullElse(column.getTable().getSchema(), syntax.getDefaultSchema())
    );
    params.put(SyntaxPlaceholders.TABLE_NAME, column.getTable().getName());
    params.put(SyntaxPlaceholders.COLUMN_NAME, column.getName());

    return StringSubstitutor.replace(
        syntax.getTemplates().getNotNullConstraint().getValue(),
        params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END
    );
  }
}
