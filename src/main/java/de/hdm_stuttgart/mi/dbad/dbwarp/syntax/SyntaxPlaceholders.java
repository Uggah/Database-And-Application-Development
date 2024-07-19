package de.hdm_stuttgart.mi.dbad.dbwarp.syntax;

public final class SyntaxPlaceholders {

  private SyntaxPlaceholders() {
  }

  public static final String PLACEHOLDER_BEGIN = "${";
  public static final String PLACEHOLDER_END = "}";

  /**
   * Placeholder for the table name.
   */
  public static final String TABLE_NAME = "table_name";

  /**
   * Placeholder for the full table name, including the schema.
   */
  public static final String FULL_TABLE_NAME = "full_table_name";

  /**
   * Placeholder for the column definitions.
   */
  public static final String COLUMN_DEFINITIONS = "column_definitions";

  // COLUMN DEFINITIONS

  /**
   * Placeholder for the column name.
   */
  public static final String COLUMN_NAME = "column_name";

  /**
   * Placeholder for the column type.
   */
  public static final String COLUMN_TYPE = "column_type";

  /**
   * Placeholder for the end of block constraints.
   */
  public static final String END_OF_BLOCK_CONSTRAINTS = "end_of_block_constraints";

  /**
   * Placeholder for the end of line constraints.
   */
  public static final String END_OF_LINE_CONSTRAINTS = "end_of_line_constraints";

  // CONSTRAINT DEFINITIONS

  /**
   * Placeholder for the column names.
   */
  public static final String COLUMN_NAMES = "column_names";

  /**
   * Placeholder for the foreign table name.
   */
  public static final String FOREIGN_TABLE_NAME = "foreign_table_name";

  /**
   * Placeholder for the foreign column names.
   */
  public static final String FOREIGN_COLUMN_NAMES = "foreign_column_names";

}
