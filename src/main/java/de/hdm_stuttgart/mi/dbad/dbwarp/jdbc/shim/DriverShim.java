package de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.shim;

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
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import lombok.Builder;
import lombok.extern.slf4j.XSlf4j;

/**
 * DriverShim forwards all method calls to an underlying delegate. This is needed to load external
 * JDBC drivers. See: <a href="https://www.kfu.com/~nsayer/Java/dyn-jdbc.html">Pick your JDBC driver
 * at runtime</a>
 */
@XSlf4j
@Builder
public class DriverShim implements Driver {

  private final Driver delegate;

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    log.entry(url, info);
    return log.exit(delegate.connect(url, info));
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    log.entry(url);
    return log.exit(delegate.acceptsURL(url));
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    log.entry(url, info);
    return log.exit(delegate.getPropertyInfo(url, info));
  }

  @Override
  public int getMajorVersion() {
    log.entry();
    return log.exit(delegate.getMajorVersion());
  }

  @Override
  public int getMinorVersion() {
    log.entry();
    return log.exit(delegate.getMinorVersion());
  }

  @Override
  public boolean jdbcCompliant() {
    log.entry();
    return log.exit(delegate.jdbcCompliant());
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    log.entry();
    return log.exit(delegate.getParentLogger());
  }
}
