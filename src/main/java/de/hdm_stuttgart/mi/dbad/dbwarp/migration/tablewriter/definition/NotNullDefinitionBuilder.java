package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;

/**
 * Interface for classes that build SQL NOT NULL definition statements for columns.
 */
public interface NotNullDefinitionBuilder {

  /**
   * Generates a SQL NOT NULL definition statement for a given column
   *
   * @param column The column for which to generate the NOT NULL definition statement
   * @return A SQL statement string that defines the NOT NULL constraint for the column
   */
  String createNotNullDefinitionStatement(final Column column);


}
