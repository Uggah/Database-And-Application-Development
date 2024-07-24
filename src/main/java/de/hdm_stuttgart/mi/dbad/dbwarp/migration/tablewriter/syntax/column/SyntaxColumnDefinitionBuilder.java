package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.GenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.NotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintDefinitionStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL column definition statements based on a specific syntax. This
 * class is responsible for constructing the SQL statement for a column definition, including any
 * inline constraints, according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxColumnDefinitionBuilder implements ColumnDefinitionBuilder {

  private final Syntax syntax;
  private final ConstraintDefinitionBuilder<PrimaryKeyConstraint> primaryKeyConstraintConstraintDefinitionBuilder;
  private final ConstraintDefinitionBuilder<ForeignKeyConstraint> foreignKeyConstraintConstraintDefinitionBuilder;
  private final ConstraintDefinitionBuilder<UniqueConstraint> uniqueConstraintConstraintDefinitionBuilder;
  private final NotNullDefinitionBuilder notNullDefinitionBuilder;
  private final GenerationStrategyDefinitionBuilder generationStrategyDefinitionBuilder;

  /**
   * Generates a SQL column definition statement for a given column. This method takes into account
   * the column's properties and any applicable constraints, constructing a statement that is
   * compliant with the provided syntax.
   *
   * @param column The column for which to generate the definition statement.
   * @return A SQL statement string that defines the column within a table.
   */
  @Override
  public String createColumnDefinitionStatement(Column column) {
    log.entry(column);

    final List<String> endOfLineConstraints = new ArrayList<>();

    // PRIMARY KEY CONSTRAINT
    if (SyntaxHelper.getPrimaryKeyStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE
        && column.getPrimaryKey() != null) {
      endOfLineConstraints.add(
          primaryKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
              column.getPrimaryKey())
      );
    }

    // FOREIGN KEY CONSTRAINT
    if (SyntaxHelper.getForeignKeyStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE) {
      final List<ForeignKeyConstraint> foreignKeyConstraints = column.getReferencingForeignKeys();

      foreignKeyConstraints.forEach(foreignKey ->
          endOfLineConstraints.add(
              foreignKeyConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
                  foreignKey)
          )
      );
    }

    // UNIQUE CONSTRAINT
    if (SyntaxHelper.getUniqueStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE) {
      final List<UniqueConstraint> uniqueConstraints = column.getUniqueConstraints();

      uniqueConstraints.forEach(uniqueConstraint ->
          endOfLineConstraints.add(
              uniqueConstraintConstraintDefinitionBuilder.createConstraintDefinitionStatement(
                  uniqueConstraint)
          )
      );
    }

    // NOT NULL CONSTRAINT
    if (SyntaxHelper.getNotNullStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE
        && Boolean.FALSE.equals(column.getNullable())) {
      endOfLineConstraints.add(
          notNullDefinitionBuilder.createNotNullDefinitionStatement(column)
      );
    }

    final Map<String, String> params = new HashMap<>();

    // COLUMN DEFAULT

    if (column.getDefaultValue() != null) {
      final String defaultValueDefinition = StringSubstitutor.replace(
          syntax.getTemplates().getColumnDefault(),
          Map.of(SyntaxPlaceholders.DEFAULT_VALUE, column.getDefaultValue()),
          SyntaxPlaceholders.PLACEHOLDER_BEGIN,
          SyntaxPlaceholders.PLACEHOLDER_END
      );

      params.put(SyntaxPlaceholders.COLUMN_DEFAULT, defaultValueDefinition);
    } else {
      params.put(SyntaxPlaceholders.COLUMN_DEFAULT, "");
    }

    // GENERATION STRATEGY

    final GenerationStrategy generationStrategy = column.getGenerationStrategy();

    if (generationStrategy != GenerationStrategy.NONE &&
        SyntaxHelper.getGenerationStrategy(syntax, generationStrategy)
            == ConstraintDefinitionStrategy.END_OF_LINE) {
      params.put(
          SyntaxPlaceholders.COLUMN_GENERATION,
          generationStrategyDefinitionBuilder.createGenerationStrategyDefinitionStatement(column)
      );
    } else {
      params.put(SyntaxPlaceholders.COLUMN_GENERATION, "");
    }

    params.put(SyntaxPlaceholders.COLUMN_NAME, column.getName());
    params.put(SyntaxPlaceholders.COLUMN_TYPE,
        column.getType().getName()); // TODO: Implement configurable type mappings!
    params.put(SyntaxPlaceholders.END_OF_LINE_CONSTRAINTS, String.join(" ", endOfLineConstraints));

    final String out = StringSubstitutor.replace(
        syntax.getTemplates().getColumnDefinition(), params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END);

    return log.exit(out);
  }
}
