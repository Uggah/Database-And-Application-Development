package de.hdm_stuttgart.mi.dbad.dbwarp.validation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import de.hdm_stuttgart.mi.dbad.dbwarp.DBWarpCLI;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

class DBWarpCLIValidatorTest {

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
        "The following errors occurred while validating CLI parameters:\ntarget must not be blank",
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
        "The following errors occurred while validating CLI parameters:\nsource must not be blank",
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
        "The following errors occurred while validating CLI parameters:\nThe source and target database must not be the same.",
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
        "The following errors occurred while validating CLI parameters:\nSQLite does not support multiple schemas. You must provide the --schema option when the target is an SQLite database.",
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
        "The following errors occurred while validating CLI parameters:\nSQLite does not support multiple schemas. Therefore, you may not provide the --schema option when the source is an SQLite database.",
        parameterException.getMessage());
  }


}