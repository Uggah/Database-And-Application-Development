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
 * Internal model for a primary key constraint.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrimaryKeyConstraint extends Constraint {

  /**
   * Name of the constraint
   */
  private final String name;

  /**
   * {@link Table} on which the constraint is defined.
   */
  private final Table table;

  /**
   * {@link List} of {@link Column columns} which the constraint includes.
   */
  private final List<Column> columns = new ArrayList<>();

  public PrimaryKeyConstraint(String name, Table table) {
    this.name = name;
    this.table = table;
  }

  public PrimaryKeyConstraint(String name, Table table, List<Column> columns) {
    this(name, table);
    this.columns.addAll(columns);
  }

  /**
   * Gets the name of the primary key constraint. If no name is set, a default name is generated.
   *
   * @return Name of the primary key constraint.
   */
  public String getName() {
    if (name == null || name.isBlank()) {
      return String.format("PK_%s",
          table.getName()
      );
    }

    return name;
  }
}
