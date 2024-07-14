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
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
  public void close() throws Exception {
    log.entry();
    this.preparedStatementUniqueConstraints.close();
    log.exit();
  }
}
