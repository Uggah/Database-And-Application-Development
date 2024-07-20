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
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
public class UniqueConstraint extends Constraint {

  /**
   * {@link Table} on which the constraint is defined.
   */
  @ToString.Exclude
  private final Table table;

  /**
   * Name of the constraint.
   */
  private final String name;

  /**
   * {@link List} of {@link Column columns} which the constraint includes.
   */
  private final List<Column> columns = new ArrayList<>();

  /**
   * Adds a single {@link Column}.
   *
   * @param column {@link Column} to add.
   */
  public void addColumn(final Column column) {
    columns.add(column);
  }

  /**
   * Adds {@link Column columns}.
   *
   * @param columns {@link Column Columns} to add.
   */
  public void addColumns(final Iterable<Column> columns) {
    columns.forEach(this.columns::add);
  }

  /**
   * Constructor.
   *
   * @param name  Name of the constraint.
   * @param table {@link Table} on which the constraint is defined.
   */
  public UniqueConstraint(String name, Table table) {
    this.name = name;
    this.table = table;
  }

  /**
   * Constructor.
   *
   * @param name    Name of the constraint.
   * @param table   {@link Table} on which the constraint is defined.
   * @param columns {@link List} of {@link Column columns} which the constraint includes.
   */
  public UniqueConstraint(String name, Table table, List<Column> columns) {
    this(name, table);
    this.columns.addAll(columns);
  }

  /**
   * Gets the name of the unique constraint. If no name is set, a default name is generated.
   *
   * @return Name of the unique constraint.
   */
  public String getName() {
    if (name == null || name.isBlank()) {
      return String.format("UQ_%s_on_%s",
          table.getName(),
          columns.stream().map(Column::getName).reduce((a, b) -> a + "_" + b).orElse("")
      );
    }

    return name;
  }
}
