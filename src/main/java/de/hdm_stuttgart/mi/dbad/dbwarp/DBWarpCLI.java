package de.hdm_stuttgart.mi.dbad.dbwarp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
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
import java.util.concurrent.Callable;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@XSlf4j
public class DBWarpCLI implements Callable<Integer> {

  @SuppressWarnings("unused")
  @Spec
  private CommandSpec spec;

  @SuppressWarnings("unused")
  @NotBlank
  @Parameters(index = "0", description = "JDBC connection URL of source database")
  private String source;

  @SuppressWarnings("unused")
  @NotBlank
  @Parameters(index = "1", description = "JDBC connection URL of target database")
  private String target;

  @SuppressWarnings("unused")
  @Option(names = {"--verbose",
      "-v"}, description = "Verbose output", defaultValue = "false")
  private boolean verbose;

  @SuppressWarnings("unused")
  @Option(names = {"--trace",
      "-vv"}, description = "Tracing output", defaultValue = "false")
  private boolean trace;

  @SuppressWarnings("unused")
  @Option(names = {"--drivers",
      "-D"}, description = "Comma-separated paths to JAR-files containing JDBC drivers to load dynamically", split = ",")
  private String[] drivers;

  @Override
  public Integer call() {
    log.entry();
    setupLogging();
    setupDrivers();

    new DBWarpCLIValidator(spec.commandLine()).validate(this);

    MigrationManager.getInstance()
        .setConnectionManager(new DefaultConnectionManager(source, target));

    try {
      MigrationManager.getInstance().migrate();
    } catch (SQLException ex) {
      log.error("Exception thrown:", ex);

      return log.exit(1);
    }

    return log.exit(0);
  }

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

  public void setupDrivers() {
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
}
