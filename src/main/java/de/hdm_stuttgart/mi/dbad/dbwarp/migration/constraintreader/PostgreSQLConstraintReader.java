package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class PostgreSQLConstraintReader extends AbstractConstraintReader implements AutoCloseable {

  private final PreparedStatement preparedStatementUniqueConstraints;

  public PostgreSQLConstraintReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);

    preparedStatementUniqueConstraints = this.connection.prepareStatement(
        """
            SELECT
              at.attname AS column_name,
              cn.conname AS constraint_name
              FROM pg_catalog.pg_constraint cn
              LEFT JOIN pg_catalog.pg_attribute at ON at.attrelid = cn.conrelid AND at.attnum = ANY(cn.conkey)
                WHERE cn.conrelid = (SELECT oid FROM pg_catalog.pg_class WHERE relname LIKE ?)
                AND cn.contype = 'u';
            """
    );

    log.exit();
  }

  @Override
  protected void retrievePrimaryKeyConstraint(List<Table> tableList) throws SQLException {
    log.entry(tableList);
    log.exit();
  }

  @Override
  protected void retrieveForeignKeyConstraints(List<Table> tableList) throws SQLException {
    log.entry(tableList);
  }

  @Override
  protected void retrieveUniqueConstraints(List<Table> tableList) throws SQLException {
    log.entry();

    for (final Table table : tableList) {
      preparedStatementUniqueConstraints.setString(1, table.getName());
      final ResultSet allUniqueConstraints = preparedStatementUniqueConstraints.executeQuery();
      final List<UniqueConstraint> constraints = new ArrayList<>();

      while (allUniqueConstraints.next()) {
        final String constraintName = allUniqueConstraints.getString("constraint_name");
        final String columnName = allUniqueConstraints.getString("column_name");

        final Optional<UniqueConstraint> existingConstraintOptional = constraints.stream()
            .filter(constraint -> constraint.getName().equals(constraintName))
            .findAny();

        existingConstraintOptional.ifPresentOrElse(
            constraint -> constraint.addColumn(table.getColumnByName(columnName)),
            () -> {
              final UniqueConstraint constraint = new UniqueConstraint(
                  table,
                  constraintName
              );

              constraint.addColumn(
                  table.getColumnByName(columnName)
              );

              constraints.add(constraint);
            }
        );
      }

      table.addConstraints(constraints);
    }

    log.exit();
  }

  @Override
  public void close() throws Exception {
    log.entry();
    this.preparedStatementUniqueConstraints.close();
    log.exit();
  }
}
