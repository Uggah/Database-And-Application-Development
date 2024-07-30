package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

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
import java.sql.SQLException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MariaDBContainer;

public class MariaDBProvider extends DatabaseProvider {

  private final MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>(
      "mariadb:11.4.2-noble");
  private Connection connection;

  @Override
  public void initialize(InitializeDatabase initializeDatabase) throws SQLException {
    if (initializeDatabase != null && !initializeDatabase.value().isEmpty()) {
      mariaDBContainer.withInitScript(initializeDatabase.value());
    }

    // For testing with MariaDB we need access to multiple schemas.
    // This is only possible with root privileges by default.
    mariaDBContainer.withUsername("root");

    // The password must be empty because we do not know the password when
    // switching to a different database.
    mariaDBContainer.withPassword("");

    mariaDBContainer.start();

    this.connection = DriverManager.getConnection(
        mariaDBContainer.getJdbcUrl(),
        mariaDBContainer.getUsername(),
        mariaDBContainer.getPassword()
    );
  }

  @Override
  public Connection getConnection() {
    return this.connection;
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    this.connection.close();
    mariaDBContainer.stop();
  }
}
