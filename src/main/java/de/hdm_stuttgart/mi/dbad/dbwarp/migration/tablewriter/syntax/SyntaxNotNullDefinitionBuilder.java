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
