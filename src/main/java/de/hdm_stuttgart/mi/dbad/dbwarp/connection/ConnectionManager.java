package de.hdm_stuttgart.mi.dbad.dbwarp.connection;

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

import java.sql.Connection;

/**
 * Can be used to get {@link Connection Connections} to the source and target database.
 * Thread-safety (and connection pooling) depends on the implementation.
 */
public interface ConnectionManager {

  /**
   * Gets a connection to the source database.
   *
   * @return The connection to the source database.
   */
  Connection getSourceDatabaseConnection();

  /**
   * Gets a connection to the target database.
   *
   * @return The connection to the target database.
   */
  Connection getTargetDatabaseConnection();

}
