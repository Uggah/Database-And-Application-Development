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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * Implements a generic approach to read constraints from SQL databases.
 */
@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractConstraintReader implements ConstraintReader {

  protected final Connection connection;

  protected AbstractConstraintReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  /**
   * Template method for reading constraints from a {@link Table}. Calls the
   * {@link this#retrievePrimaryKeyConstraint(Table)},
   * {@link this#retrieveForeignKeyConstraints(Table)} and
   * {@link this#retrieveUniqueConstraints(Table)} methods that need to be implemented by child
   * classes.
   *
   * @param table {@link Table} to read {@link Constraint Constraints} from.
   * @return {@link List} of {@link Constraint Constraints}.
   * @throws SQLException if an SQL error occurs.
   */
  @Override
  public void readConstraints(List<Table> tableList) throws SQLException {
    log.entry(tableList);

    retrievePrimaryKeyConstraint(tableList);
    retrieveUniqueConstraints(tableList);
    retrieveForeignKeyConstraints(tableList);

  }

  protected abstract void retrievePrimaryKeyConstraint(List<Table> tableList)
      throws SQLException;

  protected abstract void retrieveForeignKeyConstraints(
      List<Table> tableList)
      throws SQLException;

  protected abstract void retrieveUniqueConstraints(List<Table> tableList)
      throws SQLException;

}
