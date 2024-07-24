package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.table;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.GenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.NotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.TableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintDefinitionStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL table definition statements based on a specific syntax. This
 * class is responsible for constructing the SQL statement for a table definition, including any
 * constraints, according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxTableDefinitionBuilder implements TableDefinitionBuilder {

  private final Syntax syntax;
  private final ColumnDefinitionBuilder columnDefinitionBuilder;
  private final ConstraintDefinitionBuilder<PrimaryKeyConstraint> primaryKeyConstraintConstraintDefinitionBuilder;
  private final ConstraintDefinitionBuilder<ForeignKeyConstraint> foreignKeyConstraintConstraintDefinitionBuilder;
  private final ConstraintDefinitionBuilder<UniqueConstraint> uniqueConstraintConstraintDefinitionBuilder;
  private final NotNullDefinitionBuilder notNullDefinitionBuilder;
  private final GenerationStrategyDefinitionBuilder generationStrategyDefinitionBuilder;

  /**
   * Generates a SQL table definition statement for a given table. This method takes into account
   * the table's properties and any applicable constraints, constructing a statement that is
   * compliant with the provided syntax.
   *
   * @param table The table for which to generate the definition statement.
   * @return A SQL statement string that defines the table.
   */
  @Override
  public String createTableDefinitionStatement(Table table) {
    log.entry(table);

    final Map<String, String> params = new HashMap<>();
    params.put(SyntaxPlaceholders.TABLE_NAME, table.getName());
    params.put(SyntaxPlaceholders.FULL_TABLE_NAME, table.getSchema() + "." + table.getName());

    final List<String> columnDefinitions = table.getColumns().stream()
        .map(columnDefinitionBuilder::createColumnDefinitionStatement)
        .toList();

    params.put(SyntaxPlaceholders.COLUMN_DEFINITIONS, String.join(", ", columnDefinitions));

    final List<String> endOfBlockConstraints = new ArrayList<>();

    // PRIMARY KEY

    if (SyntaxHelper.getPrimaryKeyStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_BLOCK
        && table.getPrimaryKeyConstraint() != null) {
      endOfBlockConstraints.add(
          primaryKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
              table.getPrimaryKeyConstraint())
      );
    }

    // FOREIGN KEYS

    if (SyntaxHelper.getForeignKeyStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_BLOCK) {
      final List<String> constraintDefinitions = table.getForeignKeyConstraints().stream()
          .map(foreignKeyConstraintConstraintDefinitionBuilder::createConstraintDefinitionStatement)
          .toList();

      endOfBlockConstraints.addAll(constraintDefinitions);
    }

    // UNIQUE

    if (SyntaxHelper.getUniqueStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_BLOCK) {
      final List<String> constraintDefinitions = table.getUniqueConstraints().stream()
          .map(uniqueConstraintConstraintDefinitionBuilder::createConstraintDefinitionStatement)
          .toList();

      endOfBlockConstraints.addAll(constraintDefinitions);
    }

    // NOT NULL

    if (SyntaxHelper.getNotNullStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_BLOCK) {
      final List<String> constraintDefinitions = table.getColumns().stream()
          .filter(column -> Boolean.FALSE.equals(column.getNullable()))
          .map(notNullDefinitionBuilder::createNotNullDefinitionStatement).toList();

      endOfBlockConstraints.addAll(constraintDefinitions);
    }

    // GENERATION STRATEGY

    for (final Column column : table.getColumns()) {
      final GenerationStrategy generationStrategy = column.getGenerationStrategy();

      if (generationStrategy != GenerationStrategy.NONE &&
          SyntaxHelper.getGenerationStrategy(syntax, generationStrategy)
              == ConstraintDefinitionStrategy.END_OF_BLOCK) {
        final String generationConstraint = generationStrategyDefinitionBuilder
            .createGenerationStrategyDefinitionStatement(column);

        endOfBlockConstraints.add(generationConstraint);
      }
    }

    params.put(SyntaxPlaceholders.SCHEMA_NAME,
        Objects.requireNonNullElse(table.getSchema(), syntax.getDefaultSchema()));

    params.put(SyntaxPlaceholders.END_OF_BLOCK_CONSTRAINTS,
        String.join(", ", endOfBlockConstraints));

    if (!endOfBlockConstraints.isEmpty()) {
      params.compute(SyntaxPlaceholders.COLUMN_DEFINITIONS, (k, v) -> v + ",");
    }

    final String out = StringSubstitutor.replace(
        syntax.getTemplates().getCreateTable(), params, PLACEHOLDER_BEGIN,
        PLACEHOLDER_END);

    return log.exit(out);
  }
}
