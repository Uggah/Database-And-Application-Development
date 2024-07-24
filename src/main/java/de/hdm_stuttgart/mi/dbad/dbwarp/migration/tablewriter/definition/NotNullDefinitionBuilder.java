package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;

public interface NotNullDefinitionBuilder {

  /**
   * Generates a SQL NOT NULL definition statement for a given column
   *
   * @param column The column for which to generate the NOT NULL definition statement
   * @return A SQL statement string that defines the NOT NULL constraint for the column
   */
  String createNotNullDefinitionStatement(final Column column);


}
