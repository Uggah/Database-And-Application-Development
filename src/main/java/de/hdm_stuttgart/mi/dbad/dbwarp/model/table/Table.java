package de.hdm_stuttgart.mi.dbad.dbwarp.model.table;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.XSlf4j;

/**
 * This is the internal representation of a database table. It is used as an intermediate format for
 * the migration process.
 */
@Data
@XSlf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Table {

  /**
   * Schema in which this table resides in.
   */
  @ToString.Include
  @EqualsAndHashCode.Include
  private final String schema;

  /**
   * Name of the table.
   */
  @ToString.Include
  @EqualsAndHashCode.Include
  private final String name;

  /**
   * Type of the table.
   */
  @ToString.Include
  @EqualsAndHashCode.Include
  private final TableType type;

  /**
   * {@link List} of all {@link Column} definitions in this table.
   */
  private final List<Column> columns = new ArrayList<>();

  /**
   * {@link PrimaryKeyConstraint} in this table.
   */
  private PrimaryKeyConstraint primaryKeyConstraint;

  /**
   * {@link List} of all {@link UniqueConstraint} definitions in this table.
   */
  private final List<UniqueConstraint> uniqueConstraints = new ArrayList<>();

  /**
   * {@link List} of all {@link ForeignKeyConstraint} definitions in this table.
   */
  private final List<ForeignKeyConstraint> foreignKeyConstraints = new ArrayList<>();

  /**
   * Adds a {@link Column} to the model.
   *
   * @param column {@link Column} to add
   */
  public void addColumn(Column column) {
    log.entry(column);
    columns.add(column);
    log.exit();
  }

  /**
   * Adds multiple {@link Column Columns} to the model.
   *
   * @param columns {@link Iterable} of {@link Column Columns} to add
   */
  public void addColumns(Iterable<Column> columns) {
    log.entry(columns);
    columns.forEach(this::addColumn);
    log.exit();
  }

  /**
   * Adds a {@link ForeignKeyConstraint ForeignKeyConstraint} to the model.
   *
   * @param constraint {@link ForeignKeyConstraint} to add
   */
  public void addForeignKeyConstraint(ForeignKeyConstraint constraint) {
    log.entry(constraint);
    foreignKeyConstraints.add(constraint);
    log.exit();
  }

  /**
   * Adds multiple {@link ForeignKeyConstraint ForeignKeyConstraints} to the model.
   *
   * @param constraints {@link Iterable} of {@link ForeignKeyConstraint ForeignKeyConstraints} to
   *                    add
   */
  public void addForeignKeyConstraints(Iterable<ForeignKeyConstraint> constraints) {
    log.entry(constraints);
    constraints.forEach(this::addForeignKeyConstraint);
    log.exit();
  }

  /**
   * Adds a single {@link UniqueConstraint} to the model.
   *
   * @param constraint {@link UniqueConstraint} to add
   */
  public void addUniqueConstraint(UniqueConstraint constraint) {
    log.entry(constraint);
    uniqueConstraints.add(constraint);
    log.exit();
  }

  /**
   * Adds multiple {@link UniqueConstraint UniqueConstraints} to the model.
   *
   * @param constraints {@link Iterable} of {@link UniqueConstraint UniqueConstraints} to add
   */
  public void addUniqueConstraints(Iterable<UniqueConstraint> constraints) {
    log.entry(constraints);
    constraints.forEach(this::addUniqueConstraint);
    log.exit();
  }

  /**
   * Gets a {@link Column} in this table by its name.
   *
   * @param name Name of the column to get.
   * @return the {@link Column} with the given name, or if it does not exist, {@code null}.
   */
  public Column getColumnByName(String name) {
    return columns.parallelStream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
  }

}
