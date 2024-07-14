package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;

/**
 * Interface for classes that create table definition statements.
 */
public interface TableDefinitionBuilder {

  /**
   * Creates a table definition statement for the given table.
   *
   * @param table The table
   * @return The table definition statement
   */
  String createTableDefinitionStatement(final Table table);

}
