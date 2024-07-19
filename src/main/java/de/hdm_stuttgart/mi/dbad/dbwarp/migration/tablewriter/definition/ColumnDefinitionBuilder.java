package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;

public interface ColumnDefinitionBuilder {

  /**
   * Creates a column definition statement for the given column.
   *
   * @param column The column
   * @return The column definition statement
   */
  String createColumnDefinitionStatement(final Column column);

}
