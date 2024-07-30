package de.hdm_stuttgart.mi.dbad.dbwarp.validation;

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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import de.hdm_stuttgart.mi.dbad.dbwarp.DBWarpCLI;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

class DBWarpCLIValidatorTest {

  static {
    Locale.setDefault(Locale.US);
  }

  @Test
  void testValidate_NoViolation() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();
    cli.setTarget("some:target");
    cli.setSource("some:source");

    assertDoesNotThrow(() -> validator.validate(cli));
  }

  @Test
  void testValidate_MissingTarget() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();
    cli.setSource("some:source");

    final ParameterException parameterException = assertThrows(ParameterException.class,
        () -> validator.validate(cli));
    assertEquals(
        "The following errors occurred while validating CLI parameters:" + System.lineSeparator()
            + "target must not be blank",
        parameterException.getMessage());
  }

  @Test
  void testValidate_MissingSource() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();
    cli.setTarget("some:target");

    final ParameterException parameterException = assertThrows(ParameterException.class,
        () -> validator.validate(cli));
    assertEquals(
        "The following errors occurred while validating CLI parameters:" + System.lineSeparator()
            + "source must not be blank",
        parameterException.getMessage());
  }

  @Test
  void testValidate_MissingTargetAndSource() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();

    final ParameterException parameterException = assertThrows(ParameterException.class,
        () -> validator.validate(cli));
    assertThat(parameterException.getMessage(), containsString("source must not be blank"));
    assertThat(parameterException.getMessage(), containsString("target must not be blank"));
    assertThat(parameterException.getMessage(),
        containsString("The source and target database must not be the same."));
  }

  @Test
  void testValidate_SameTargetAndSource() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();
    cli.setTarget("jdbc:sqlite:./test.db");
    cli.setSource("jdbc:sqlite:./test.db");

    final ParameterException parameterException = assertThrows(ParameterException.class,
        () -> validator.validate(cli));
    assertEquals(
        "The following errors occurred while validating CLI parameters:" + System.lineSeparator()
            + "The source and target database must not be the same.",
        parameterException.getMessage());
  }

  @Test
  void testValidate_NoSchemaSpecifiedWithSQLiteTarget() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();
    cli.setSource("some:source");
    cli.setTarget("jdbc:sqlite:some:target");

    final ParameterException parameterException = assertThrows(ParameterException.class,
        () -> validator.validate(cli));
    assertEquals(
        "The following errors occurred while validating CLI parameters:" + System.lineSeparator()
            + "SQLite does not support multiple schemas. You must provide the --schema option when the target is an SQLite database.",
        parameterException.getMessage());
  }

  @Test
  void testValidate_SchemaSpecifiedWithSQLiteSource() {
    final CommandLine commandLine = mock(CommandLine.class);

    DBWarpCLIValidator validator = new DBWarpCLIValidator(commandLine);
    DBWarpCLI cli = new DBWarpCLI();
    cli.setTarget("some:target");
    cli.setSource("jdbc:sqlite:some:source");
    cli.setSchema("not_null");

    final ParameterException parameterException = assertThrows(ParameterException.class,
        () -> validator.validate(cli));
    assertEquals(
        "The following errors occurred while validating CLI parameters:" + System.lineSeparator()
            + "SQLite does not support multiple schemas. Therefore, you may not provide the --schema option when the source is an SQLite database.",
        parameterException.getMessage());
  }


}
