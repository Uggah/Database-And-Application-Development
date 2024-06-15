package de.hdm_stuttgart.mi.dbad.dbwarp.jdbc;

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
      "h2",
      "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar",
      "firebird",
      "https://repo1.maven.org/maven2/org/firebirdsql/jdbc/jaybird/5.0.4.java11/jaybird-5.0.4.java11.jar"
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
  void testLoadDriver_H2() throws SQLException {
    final Driver driver = driverLoaderMap.get("h2").loadDriver();

    assertEquals(2, driver.getMajorVersion());
    assertEquals(2, driver.getMinorVersion());
    assertTrue(driver.acceptsURL("jdbc:h2:mem:db1"));
  }

  @Test
  void testLoadDriver_Firebird() throws SQLException {
    final Driver driver = driverLoaderMap.get("firebird").loadDriver();

    assertEquals(5, driver.getMajorVersion());
    assertEquals(0, driver.getMinorVersion());
    assertTrue(driver.acceptsURL("jdbc:firebird:localhost/3050:/some/database/path"));
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
