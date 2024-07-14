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

import de.hdm_stuttgart.mi.dbad.dbwarp.DBWarpCLI;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

/**
 * Is used to validate {@link DBWarpCLI}'s arguments.
 */
@RequiredArgsConstructor
public class DBWarpCLIValidator implements ClassValidator<DBWarpCLI> {

  /**
   * The {@link CommandLine} instance used to create a {@link ParameterException} on constraint
   * violation.
   */
  private final CommandLine commandLine;

  /**
   * Validates the given instance of {@link DBWarpCLI} using Bean Validation annotations. If one or
   * more {@link ConstraintViolation} is found, their messages are joined using newline characters
   * and wrapped inside a {@link ParameterException}. See: <a
   * href="https://jakarta.ee/specifications/bean-validation/3.0/apidocs/">Jakarta Bean Validation
   * 3.0 Javadoc</a>
   *
   * @param cli instance of {@link DBWarpCLI} to validate.
   * @throws ValidationException if a {@link ConstraintViolation} has been found.
   */
  @Override
  public void validate(final DBWarpCLI cli) throws ValidationException {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      final Validator validator = validatorFactory.getValidator();

      final Set<ConstraintViolation<DBWarpCLI>> violationSet = validator.validate(cli);

      final List<String> errors = violationSet.stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.toList());

      // Check if target DBMS is SQLite and no schema is set.
      if (cli.getTarget().startsWith("jdbc:sqlite") && cli.getSchema() == null) {
        errors.add(
            "SQLite does not support multiple schemas. You must provide the --schema option when the target is an SQLite database."
        );
      }

      if (cli.getSource().equals(cli.getTarget())) {
        errors.add(
            "The source and target database must not be the same."
        );
      }

      if (errors.isEmpty()) {
        return;
      }

      final String errorMessage = String.join("\n", errors);

      throw new ParameterException(commandLine,
          String.format("The following errors occurred while validating CLI parameters:%n%s",
              errorMessage));
    }


  }
}
