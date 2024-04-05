package de.hdm_stuttgart.mi.dbad.dbwarp.validation;

import de.hdm_stuttgart.mi.dbad.dbwarp.DBWarpCLI;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

@RequiredArgsConstructor
public class DBWarpCLIValidator implements ClassValidator<DBWarpCLI> {

  private final CommandLine commandLine;

  @Override
  public void validate(final DBWarpCLI cli) throws ValidationException {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      final Validator validator = validatorFactory.getValidator();

      final Set<ConstraintViolation<DBWarpCLI>> violationSet = validator.validate(cli);

      if (violationSet.isEmpty()) {
        return;
      }

      final String errorMessage = violationSet.stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.joining("\n"));

      throw new ParameterException(commandLine,
              String.format("The following errors occurred while validating CLI parameters:%n%s",
              errorMessage));
    }


  }
}
