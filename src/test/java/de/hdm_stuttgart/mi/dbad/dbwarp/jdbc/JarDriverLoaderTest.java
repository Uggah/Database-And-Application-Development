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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JarDriverLoaderTest {

  private static final Map<String, String> JDBC_DRIVER_DOWNLOAD_URLS = Map.of(
      "postgresql", "https://jdbc.postgresql.org/download/postgresql-42.7.3.jar",
      "mariadb",
      "https://dlm.mariadb.com/3824147/Connectors/java/connector-java-3.4.0/mariadb-java-client-3.4.0.jar"
  );

  private final Map<String, DriverLoader> driverLoaderMap = new HashMap<>();

  @BeforeEach
  void beforeEach() throws URISyntaxException, IOException {
    for (Entry<String, String> driverDownloadUrl : JDBC_DRIVER_DOWNLOAD_URLS.entrySet()) {
      final URL downloadUrl = new URI(driverDownloadUrl.getValue()).toURL();
      final String driverFile = String.format("%s-driver.jar", driverDownloadUrl.getKey());

      downloadDriver(downloadUrl, driverFile);

      this.driverLoaderMap.put(driverDownloadUrl.getKey(), new JarDriverLoader(driverFile));
    }
  }

  @AfterEach
  void afterEach() {
    for (String driverName : JDBC_DRIVER_DOWNLOAD_URLS.keySet()) {
      boolean ignored = new File(String.format("%s-driver.jar", driverName)).delete();
    }
  }

  @Test
  void testLoadDriver_PostgreSQL() throws SQLException {
    final Driver driver = driverLoaderMap.get("postgresql").loadDriver();

    assertEquals(42, driver.getMajorVersion());
    assertEquals(7, driver.getMinorVersion());
    assertTrue(driver.acceptsURL("jdbc:postgresql://localhost:5173/"));
  }

  @Test
  void testLoadDriver_MariaDB() throws SQLException {
    final Driver driver = driverLoaderMap.get("mariadb").loadDriver();

    assertEquals(3, driver.getMajorVersion());
    assertEquals(3, driver.getMinorVersion());
    assertTrue(driver.acceptsURL("jdbc:mariadb://localhost:3306/"));
  }

  private void downloadDriver(URL source, String destination) throws IOException {
    try (InputStream input = source.openStream();
        BufferedInputStream bis = new BufferedInputStream(input);
        FileOutputStream output = new FileOutputStream(destination)) {

      byte[] dataBuffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = bis.read(dataBuffer, 0, 1024)) != -1) {
        output.write(dataBuffer, 0, bytesRead);
      }
    }
  }

}
