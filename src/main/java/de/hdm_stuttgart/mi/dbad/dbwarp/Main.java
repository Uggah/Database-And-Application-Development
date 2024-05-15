package de.hdm_stuttgart.mi.dbad.dbwarp;

import lombok.extern.slf4j.XSlf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@XSlf4j
@Command(name = "dbwarp", mixinStandardHelpOptions = true, version = "0.0.1-alpha")
public class Main {

  /**
   * Main method
   *
   * @param args CLI args
   */
  public static void main(String[] args) {
    log.entry((Object) args);
    log.info("Engage!");
    int exitCode = new CommandLine(new DBWarpCLI()).execute(args);

    System.exit(exitCode);
  }
}