package de.hdm_stuttgart.mi.dbad.dbwarp.jdbc;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.shim.DriverShim;

/**
 * The DriverLoader interface can be implemented by classes used to dynamically load a JDBC driver.
 */
public interface DriverLoader {

  /**
   * The loadDriver method can be used to load a driver dynamically. The returned {@link DriverShim}
   * will forward all calls to the underlying loaded {@link java.sql.Driver} instance.
   *
   * @return A {@link DriverShim} of the dynamically loaded {@link java.sql.Driver}
   */
  DriverShim loadDriver();

}
