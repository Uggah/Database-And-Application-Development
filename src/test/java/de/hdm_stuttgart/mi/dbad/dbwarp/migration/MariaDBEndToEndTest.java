package de.hdm_stuttgart.mi.dbad.dbwarp.migration;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.EndToEndProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.InitializeDatabase;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.MariaDBProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql.ProvideDatabases;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConfigProvider.class)
@ExtendWith(EndToEndProvider.class)
public class MariaDBEndToEndTest {

  @Test
  @ProvideDatabases(
      sourceProvider = MariaDBProvider.class,
      targetProvider = MariaDBProvider.class
  )
  @InitializeDatabase("mariaDB/MariaDBEndToEndTest.sql")
  void testToMariaDB(final ConnectionManager connectionManager) throws Exception {
    final MigrationManager migrationManager = MigrationManager.getInstance();
    migrationManager.setConnectionManager(connectionManager);
    migrationManager.migrate();

    final Connection targetConnection = connectionManager.getTargetDatabaseConnection();

    final Statement stmt = targetConnection.createStatement();

    final ResultSet ownerResultSet = stmt.executeQuery("SELECT * FROM test.owner");

    ownerResultSet.next();

    assertEquals(1, ownerResultSet.getInt("id"));
    assertEquals("Alice", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-01", ownerResultSet.getString("birth_date"));
    assertEquals("Wonderland", ownerResultSet.getString("address"));

    ownerResultSet.next();

    assertEquals(2, ownerResultSet.getInt("id"));
    assertEquals("Bob", ownerResultSet.getString("full_name"));
    assertEquals("1990-01-02", ownerResultSet.getString("birth_date"));
    assertEquals("Bobsville", ownerResultSet.getString("address"));

    final ResultSet petResultSet = stmt.executeQuery("SELECT * FROM test.pet");

    petResultSet.next();

    assertEquals(1, petResultSet.getInt("id"));
    assertEquals("Fluffy", petResultSet.getString("name"));
    assertEquals("2010-01-01", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(2, petResultSet.getInt("id"));
    assertEquals("Spot", petResultSet.getString("name"));
    assertEquals("2010-01-02", petResultSet.getString("birth_date"));
    assertEquals(1, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(3, petResultSet.getInt("id"));
    assertEquals("Rex", petResultSet.getString("name"));
    assertEquals("2010-01-03", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));

    petResultSet.next();

    assertEquals(4, petResultSet.getInt("id"));
    assertEquals("Fido", petResultSet.getString("name"));
    assertEquals("2010-01-04", petResultSet.getString("birth_date"));
    assertEquals(2, petResultSet.getInt("owner_id"));
  }

}
