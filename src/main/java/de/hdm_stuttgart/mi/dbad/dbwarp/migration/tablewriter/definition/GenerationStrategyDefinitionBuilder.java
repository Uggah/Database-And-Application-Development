package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;

/**
 * Interface for classes that build SQL generation strategy definition statements for columns. See:
 * {@link de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy}
 */
public interface GenerationStrategyDefinitionBuilder {

  /**
   * Generates a SQL generation strategy definition statement for a given column
   *
   * @param column The column for which to generate the generation strategy definition statement
   * @return A SQL statement string that defines the generation strategy for the column
   */
  String createGenerationStrategyDefinitionStatement(final Column column);

}
