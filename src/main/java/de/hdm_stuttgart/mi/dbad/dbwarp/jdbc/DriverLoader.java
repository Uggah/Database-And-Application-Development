package de.hdm_stuttgart.mi.dbad.dbwarp.jdbc;

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
