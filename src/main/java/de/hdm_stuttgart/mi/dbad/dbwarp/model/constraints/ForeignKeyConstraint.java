package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Internal model for a foreign key constraint.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ForeignKeyConstraint extends Constraint {

  /**
   * Name of the foreign key constraint.
   */
  private final String name;

  /**
   * {@link Table} of the table importing foreign columns.
   */
  private final Table childTable;

  /**
   * {@link Table} of the table exporting primary key columns.
   */
  private final Table parentTable;

  /**
   * {@link Column}s of the child table.
   */
  private final List<Column> childColumns = new ArrayList<>();

  /**
   * {@link Column}s of the parent table.
   */
  private final List<Column> parentColumns = new ArrayList<>();

}
