package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

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

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    params.put(SyntaxPlaceholders.SCHEMA_NAME,
        Objects.requireNonNullElse(primaryKeyConstraint.getTable().getSchema(),
            syntax.getDefaultSchema()));

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
