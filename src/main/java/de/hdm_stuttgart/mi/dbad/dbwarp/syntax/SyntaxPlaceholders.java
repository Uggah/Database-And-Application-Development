package de.hdm_stuttgart.mi.dbad.dbwarp.syntax;

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

/**
 * Class containing all syntax placeholders used in the syntax templates. This class cannot be
 * instantiated.
 */
public final class SyntaxPlaceholders {

  private SyntaxPlaceholders() {
  }

  public static final String PLACEHOLDER_BEGIN = "${";
  public static final String PLACEHOLDER_END = "}";

  /**
   * Placeholder for the schema name.
   */
  public static final String SCHEMA_NAME = "schema_name";

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

  /**
   * Placeholder for the column default value.
   */
  public static final String COLUMN_DEFAULT = "column_default";

  // COLUMN DEFAULT

  /**
   * Placeholder for the default value. The injected value will be the result of a toString call on
   * the default value.
   */
  public static final String DEFAULT_VALUE = "default_value";

  // CONSTRAINT DEFINITIONS

  /**
   * Placeholder for the constraint name.
   */
  public static final String CONSTRAINT_NAME = "constraint_name";

  /**
   * Placeholder for the column names.
   */
  public static final String COLUMN_NAMES = "column_names";

  // FOREIGN KEY SPECIFIC

  /**
   * Placeholder for the child table schema name.
   */
  public static final String CHILD_TABLE_SCHEMA_NAME = "child_table_schema_name";

  /**
   * Placeholder for the child table name.
   */
  public static final String CHILD_TABLE_NAME = "child_table_name";

  /**
   * Placeholder for the child column names. Interchangeable with {@link #COLUMN_NAMES}.
   */
  public static final String CHILD_COLUMN_NAMES = "child_column_names";

  /**
   * Placeholder for the child table schema name.
   */
  public static final String PARENT_TABLE_SCHEMA_NAME = "parent_table_schema_name";

  /**
   * Placeholder for the foreign table name.
   */
  public static final String PARENT_TABLE_NAME = "parent_table_name";

  /**
   * Placeholder for the foreign column names.
   */
  public static final String PARENT_COLUMN_NAMES = "parent_column_names";

  // COLUMN GENERATION

  /**
   * Placeholder for the column generation.
   */
  public static final String COLUMN_GENERATION = "column_generation";

  // INSERT STATEMENT

  /**
   * Placeholder for the column values.
   */
  public static final String COLUMN_VALUES = "column_values";
}
