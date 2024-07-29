package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

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
