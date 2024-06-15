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
