package de.hdm_stuttgart.mi.dbad.dbwarp;

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

import static picocli.CommandLine.Parameters.NULL_VALUE;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.hdm_stuttgart.mi.dbad.dbwarp.config.Configuration;
import de.hdm_stuttgart.mi.dbad.dbwarp.connection.DefaultConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.DriverLoader;
import de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.JarDriverLoader;
import de.hdm_stuttgart.mi.dbad.dbwarp.jdbc.exception.DriverLoadingException;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.MigrationManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.validation.DBWarpCLIValidator;
import jakarta.validation.constraints.NotBlank;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@XSlf4j
@Command(
    name = "dbwarp",
    description = """
        DBWarp is a cli application that allows you to migrate database schemas between different database management systems.
        It is the product of our semester project in the module Database and Application Development at Stuttgart Media University, course Computer Science and Media (B.Sc.).
        """,
    version = "0.0.1-alpha",
    mixinStandardHelpOptions = true
)
@Getter
public class DBWarpCLI implements Callable<Integer> {

  @SuppressWarnings("unused")
  @Spec
  private CommandSpec spec;

  /**
   * source defines the JDBC connection URL of the source database. It will be automatically
   * injected by PicoCLI.
   */
  @SuppressWarnings("unused")
  @NotBlank
  @Parameters(index = "0", description = "JDBC connection URL of source database. Must contain all credentials required to connect to the source database. Example: 'jdbc:postgresql://127.0.0.1:5432/backwards?user=USERNAME&password=PASSWORD' or 'jdbc:sqlite:./database.sqlite'. Use single quotes to escape preprocessing by the shell.")
  private String source;

  /**
   * target defines the JDBC connection URL of the target database. It will be automatically
   * injected by PicoCLI.
   */
  @SuppressWarnings("unused")
  @NotBlank
  @Parameters(index = "1", description = "JDBC connection URL of target database. Must contain all credentials required to connect to the target database. Example: 'jdbc:postgresql://127.0.0.1:5432/backwards?user=USERNAME&password=PASSWORD' or 'jdbc:sqlite:./database.sqlite'. Use single quotes to escape preprocessing by the shell.")
  private String target;

  /**
   * schema defines the schema to migrate. It will be automatically injected by PicoCLI.
   */
  @SuppressWarnings("unused")
  @Option(names = {"--schema",
      "-S"}, description = "Schema to migrate, migrates all schemas by default. Setting this option is mandatory if migrating towards an SQLite database as it only supports a single schema.", defaultValue = NULL_VALUE)
  private String schema;

  @SuppressWarnings("unused")
  @Option(names = "--syntax", description = "Syntaxes to use for migration. E.g. --syntax SQLite=./syntaxes/custom_sqlite.xml", defaultValue = NULL_VALUE)
  private Map<String, String> syntax;

  /**
   * verbose signals whether debug logging should be enabled. It will be automatically injected by
   * PicoCLI.
   */
  @SuppressWarnings("unused")
  @Option(names = {"--verbose",
      "-v"}, description = "Verbose output", defaultValue = "false")
  private boolean verbose;

  /**
   * trace signals whether trace logging should be enabled. It will be automatically injected by
   * PicoCLI.
   */
  @SuppressWarnings("unused")
  @Option(names = {"--trace",
      "-vv"}, description = "Tracing output", defaultValue = "false")
  private boolean trace;

  /**
   * drivers is an array of paths to JAR files containing JDBC drivers. They will be loaded upon
   * application initialization and registered with the {@link DriverManager}. It will be
   * automatically injected by PicoCLI.
   */
  @SuppressWarnings("unused")
  @Option(names = {"--driver",
      "-D"}, description = "Comma-separated paths to JAR-files containing JDBC drivers to load dynamically")
  private String[] drivers = new String[0];

  /**
   * call is called on every application startup. The returned {@link picocli.CommandLine.ExitCode}
   * shows the status in which the application exited.
   *
   * @return the exit status code.
   */
  @Override
  public Integer call() {
    log.entry();
    setupLogging();
    setupDrivers();
    setupConfiguration();

    new DBWarpCLIValidator(spec.commandLine()).validate(this);

    MigrationManager.getInstance()
        .setConnectionManager(new DefaultConnectionManager(source, target));

    try {
      MigrationManager.getInstance().migrate();
    } catch (Exception ex) {
      log.error("Exception thrown:", ex);

      return log.exit(1);
    }

    return log.exit(0);
  }

  /**
   * Sets up logging based on the command line options used.
   */
  private void setupLogging() {
    log.entry();
    log.debug("Setting up logging");

    final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    if (trace) {
      root.setLevel(Level.TRACE);
      log.exit();
      return;
    } else if (verbose) {
      root.setLevel(Level.DEBUG);
      log.exit();
      return;
    }

    root.setLevel(Level.WARN);
    log.exit();
  }

  /**
   * Dynamically loads drivers as specified in the command line options.
   */
  private void setupDrivers() {
    log.entry();
    log.debug("Loading drivers");

    Arrays.stream(this.drivers)
        .map(JarDriverLoader::new)
        .map(DriverLoader::loadDriver)
        .forEach(driverShim -> {
          try {
            DriverManager.registerDriver(driverShim);
          } catch (SQLException e) {
            throw new DriverLoadingException("Exception upon trying to register driver!", e);
          }
        });
  }

  /**
   * Configures the application based on the command line options used.
   */
  private void setupConfiguration() {
    final Map<String, Object> configuration = new HashMap<>();

    configuration.put("schema", this.schema);

    log.debug("Got schema: {}", this.schema);

    // This TreeMap is used to ensure that the syntax map is case-insensitive.
    final TreeMap<String, String> syntaxTreeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    if (this.syntax != null) {
      log.debug("Got custom syntax: {}", this.syntax);
      syntaxTreeMap.putAll(this.syntax);
    }

    configuration.put("syntax", syntaxTreeMap);

    Configuration.getInstance().configure(configuration);
  }
}
