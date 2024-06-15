package de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.shim;

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
