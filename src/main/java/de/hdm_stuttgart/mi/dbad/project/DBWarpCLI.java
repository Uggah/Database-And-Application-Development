package de.hdm_stuttgart.mi.dbad.project;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@XSlf4j
public class DBWarpCLI implements Callable<Integer> {

  @SuppressWarnings("unused")
  @CommandLine.Option(names = {"--verbose",
      "-v"}, description = "Verbose output", defaultValue = "false")
  private boolean verbose;

  @Override
  public Integer call() {
    log.entry();
    setupLogging();

    return log.exit(0);
  }

  private void setupLogging() {
    log.debug("Setting up logging");

    final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    if (!verbose) {
      root.setLevel(Level.WARN);
    }

    root.setLevel(Level.DEBUG);
  }
}
