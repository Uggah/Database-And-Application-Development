package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

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
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * Factory to construct a {@link SchemaReader} depending on database product name of the source
 * datbase connection.
 */
@XSlf4j
@AllArgsConstructor
public final class SchemaReaderFactory {

  private ConnectionManager connectionManager;

  /**
   * Constructs a {@link SchemaReader} depending on the database product name of the source database
   * connection in the instance's {@link ConnectionManager}.
   *
   * @return the constructed {@link SchemaReader}
   * @throws SQLException if an exception occurs during constructing a schema reader.
   */
  public SchemaReader getSchemaReader() throws SQLException {
    log.entry();
    final String databaseProductName = connectionManager.getSourceDatabaseConnection().getMetaData()
        .getDatabaseProductName();

    return log.exit(switch (databaseProductName) {
      case "SQLite" -> new SQLiteSchemaReader(connectionManager);
      case "PostgreSQL" -> new PostgreSQLSchemaReader(connectionManager);
      case "MariaDB", "MySQL" -> new MariaDBSchemaReader(connectionManager);
      default -> throw new IllegalArgumentException("Database is not supported by DBWarp");
    });
  }

}
