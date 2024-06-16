package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@RequiredArgsConstructor
public class SQLiteTableDefinitionBuilder {

  private static final String CREATE_TABLE = "CREATE TABLE %s (";

  private static final String COLUMN_DEFINITION = "%s %s%s,";

  private static final String NOT_NULL = " NOT NULL";

  private static final String PRIMARY_KEY = "PRIMARY KEY (%s),";
  private static final String FOREIGN_KEY = "FOREIGN KEY %s REFERENCES %s (%s),";
  private static final String UNIQUE_CONSTRAINT = "UNIQUE (%s),";

  private static final String END_CREATE_TABLE = ");";

  public String createTableDefinitionStatement(final Table table) {
    log.entry(table);

    final StringBuilder sb = new StringBuilder();

    sb.append(String.format(CREATE_TABLE, table.getFullName()));

    for (final Column column : table.getColumns()) {
      sb.append(createColumnDefinition(column));
    }

    for (final Constraint constraint : table.getConstraints()) {
      sb.append(createConstraintDefinition(constraint));
    }

    sb.deleteCharAt(sb.length() - 1);
    sb.append(END_CREATE_TABLE);

    return log.exit(sb.toString());
  }

  private String createColumnDefinition(final Column column) {
    log.entry(column);

    final String nullability = Boolean.FALSE.equals(column.getNullable()) ? NOT_NULL : "";

    return log.exit(String.format(COLUMN_DEFINITION, column.getName(),
        String.format("%s(%d)", column.getType().getName(), column.getSize()), nullability));
  }

  private String createConstraintDefinition(final Constraint constraint) {
    log.entry(constraint);

    if (constraint instanceof PrimaryKeyConstraint primaryKeyConstraint) {
      return log.exit(createPrimaryKeyConstraintDefinition(primaryKeyConstraint));
    }

    if (constraint instanceof UniqueConstraint uniqueConstraint) {
      return log.exit(createUniqueKeyConstraintDefinition(uniqueConstraint));
    }

    log.warn("Cannot migrate constraint to SQLite database: {}", constraint.toString());

    return log.exit("");
  }

  private String createPrimaryKeyConstraintDefinition(final PrimaryKeyConstraint constraint) {
    log.entry(constraint);

    final String columns = constraint.getColumns().stream()
        .map(Column::getName)
        .collect(Collectors.joining(","));

    return log.exit(String.format(PRIMARY_KEY, columns));
  }

  private String createUniqueKeyConstraintDefinition(final UniqueConstraint constraint) {
    log.entry(constraint);

    final String columns = constraint.getColumns().stream()
        .map(Column::getName)
        .collect(Collectors.joining(","));

    return log.exit(String.format(UNIQUE_CONSTRAINT, columns));
  }

}
