package de.hdm_stuttgart.mi.dbad.dbwarp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.MigrationManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.validation.DBWarpCLIValidator;
import jakarta.validation.constraints.NotBlank;
import java.sql.SQLException;
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

  @Override
  public Integer call() {
    log.entry();
    setupLogging();

    new DBWarpCLIValidator(spec.commandLine()).validate(this);

    MigrationManager.getInstance().setConnectionManager(new ConnectionManager(source, target));

    try {
      MigrationManager.getInstance().migrate();
    } catch (SQLException ex) {
      return log.exit(1);
    }

    return log.exit(0);
  }

  private void setupLogging() {
    log.entry();
    log.debug("Setting up logging");

    final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    if (!verbose) {
      root.setLevel(Level.WARN);
      return;
    }

    root.setLevel(Level.DEBUG);
    log.exit();
  }
}
