package de.hdm_stuttgart.mi.dbad.dbwarp.model.column;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.JDBCType;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * Internal model of a column.
 */
@Data
public class Column {

  /**
   * The {@link Column Column's} {@link Table}
   */
  @ToString.Exclude
  private final Table table;

  /**
   * The {@link Column Column's} name
   */
  private final String name;

  /**
   * The {@link Column Column's} {@link JDBCType}
   */
  private final JDBCType type;

  /**
   * If the {@link Column} is nullable. Is {@code true} if it is definitely nullable, {@code false}
   * if it <i>might</i> not be nullable. Could also be {@code null} if nullability could not be
   * determined.
   */
  private final Boolean nullable;

  /**
   * Size of the column
   */
  private final int size;

  /**
   * Default value. Is {@code null} if no default value is set.
   */
  private Object defaultValue = null;

  /**
   * Retrieves the primary key constraint that this column is a part of, if any.
   *
   * @return The {@link PrimaryKeyConstraint} object if this column is part of a primary key;
   * otherwise, null.
   */
  public PrimaryKeyConstraint getPrimaryKey() {
    if (table == null || table.getPrimaryKeyConstraint() == null || !table.getPrimaryKeyConstraint()
        .getColumns().contains(this)) {
      return null;
    }

    return table.getPrimaryKeyConstraint();
  }

  /**
   * Retrieves the foreign key constraints that this column is a part of, if any.
   *
   * @return The list of {@link ForeignKeyConstraint} objects if this column is part of a foreign
   * key; otherwise, an empty list.
   */
  public List<ForeignKeyConstraint> getReferencingForeignKeys() {
    if (table == null) {
      return Collections.emptyList();
    }

    return table.getForeignKeyConstraints().stream()
        .filter(foreignKeyConstraint -> foreignKeyConstraint.getChildColumns().contains(this))
        .toList();
  }

  /**
   * Retrieves the unique constraints that this column is a part of, if any.
   *
   * @return The list of {@link UniqueConstraint} objects if this column is part of a unique
   */
  public List<UniqueConstraint> getUniqueConstraints() {
    if (table == null) {
      return Collections.emptyList();
    }

    return table.getUniqueConstraints().stream()
        .filter(uniqueConstraint -> uniqueConstraint.getColumns().contains(this))
        .toList();
  }
}
