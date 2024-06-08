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

import de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.exception.DriverLoadingException;
import de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.shim.DriverShim;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * JarDriverLoader can be used to dynamically load a JDBC driver from a JAR-file during runtime.
 */
@XSlf4j
@RequiredArgsConstructor
public class JarDriverLoader implements DriverLoader {

  /**
   * Path to the JAR-File to load the JDBC driver from.
   */
  private final String jarPath;

  /**
   * Loads a driver from the given jarPath. The JAR-File at the given path needs to include metadata
   * about where to find the Driver class as per the Service Provider specification (See: <a
   * href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ServiceLoader.html#deploying-service-providers-on-the-class-path-heading">Deploying
   * service providers on the class path</a>).
   *
   * @return a {@link DriverShim} that will relay any method calls to the {@link Driver} obtained
   * from the JAR-file.
   */
  @Override
  public DriverShim loadDriver() {
    log.entry();

    final String driverClassName;

    try (final JarFile jarFile = new JarFile(jarPath)) {
      driverClassName = getDriverURIFromJarFile(jarFile).strip();
    } catch (IOException e) {
      throw new DriverLoadingException(
          String.format("Exception occurred while trying to load jar file at %s!", jarPath), e);
    }

    final URL jarUrl;

    try {
      final URI jarUri = new URI("jar:file:" + jarPath + "!/");
      jarUrl = jarUri.toURL();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new DriverLoadingException(e);
    }

    final Class<?> driverClass;

    try (final URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarUrl})) {
      driverClass = classLoader.loadClass(driverClassName);
    } catch (IOException e) {
      throw new DriverLoadingException("Could not instantiate class loader!", e);
    } catch (ClassNotFoundException e) {
      throw new DriverLoadingException("Driver class is non-existent!", e);
    }

    @SuppressWarnings("unchecked") final Constructor<? extends Driver> constructor = (Constructor<? extends Driver>) Arrays.stream(
            driverClass.getConstructors())
        .filter(constr -> constr.getParameters().length == 0)
        .findFirst()
        .orElseThrow(() -> new DriverLoadingException(
            "Could not find suitable constructor in Driver class!"));

    final Driver driver;

    try {
      driver = constructor.newInstance();
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new DriverLoadingException(
          String.format("Failed to instantiate found driver class %s", driverClass.getName()), e);
    }

    return DriverShim.builder()
        .delegate(driver)
        .build();
  }

  private String getDriverURIFromJarFile(final JarFile jarFile) throws IOException {
    final ZipEntry metaInf = jarFile.getEntry("META-INF/services/java.sql.Driver");
    final InputStream inputStream = jarFile.getInputStream(metaInf);

    return new String(inputStream.readAllBytes());
  }
}
