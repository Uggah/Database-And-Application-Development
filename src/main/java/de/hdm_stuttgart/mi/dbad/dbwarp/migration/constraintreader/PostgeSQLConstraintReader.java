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
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

/**
 * PostgreSQL specific implementation of {@link ConstraintReader}.
 */
@XSlf4j
public class PostgeSQLConstraintReader extends AbstractConstraintReader implements AutoCloseable {

  public PostgeSQLConstraintReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  @Override
  public List<Constraint> readConstraints(Table table) throws SQLException {
    log.entry(table);
    return log.exit(Collections.emptyList());
  }

  @Override
  protected PrimaryKeyConstraint retrievePrimaryKeyConstraint(Table table) throws SQLException {
    log.entry();
    return log.exit(null);
  }

  @Override
  protected Collection<ForeignKeyConstraint> retrieveForeignKeyConstraints(Table table)
      throws SQLException {
    log.entry();
    return log.exit(Collections.emptyList());
  }

  @Override
  protected Collection<UniqueConstraint> retrieveUniqueConstraints(Table table)
      throws SQLException {
    log.entry();
    return log.exit(Collections.emptyList());
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }
}
