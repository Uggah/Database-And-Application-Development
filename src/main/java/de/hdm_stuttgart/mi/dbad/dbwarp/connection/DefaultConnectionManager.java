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
import java.sql.DriverManager;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.XSlf4j;

/**
 * Default implementation of a {@link ConnectionManager}. Does not do connection pooling and can
 * thus not be used by multiple threads.
 */
@XSlf4j
@Getter
public class DefaultConnectionManager implements ConnectionManager {

  private final Connection sourceDatabaseConnection;

  private final Connection targetDatabaseConnection;

  @SneakyThrows
  public DefaultConnectionManager(final String source, final String target) {
    log.entry(source, target);
    this.sourceDatabaseConnection = DriverManager.getConnection(source);
    this.targetDatabaseConnection = DriverManager.getConnection(target);
    log.exit();
  }

}
