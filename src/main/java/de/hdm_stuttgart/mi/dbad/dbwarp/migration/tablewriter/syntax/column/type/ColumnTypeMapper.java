package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column.type;

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
 * Maps a JDBC-Type of a {@link Column} to a vendor specific type.
 */
public interface ColumnTypeMapper {

  /**
   * Maps a JDBC-Type of a {@link Column} to a vendor specific type.
   *
   * @param column The {@link Column} to map.
   * @return The vendor specific type.
   */
  String map(Column column);
}
