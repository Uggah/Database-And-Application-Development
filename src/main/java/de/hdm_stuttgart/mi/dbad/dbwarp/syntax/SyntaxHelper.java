package de.hdm_stuttgart.mi.dbad.dbwarp.syntax;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintType;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.InlineConstraintDefinition;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.InlineConstraintDefinitionStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.InlineConstraintDefinitionsOption;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import java.util.List;

public class SyntaxHelper {

  private SyntaxHelper() {
  }

  public static boolean isConstraintInlinable(final Syntax syntax,
      final ConstraintType constraintType) {
    if (syntax.getTableCreation().getOptions().getInlineConstraintDefinitions()
        == InlineConstraintDefinitionsOption.NEVER) {
      return false;
    }

    return syntax.getTableCreation()
        .getTemplates()
        .getInlineConstraints()
        .getConstraint()
        .stream()
        .anyMatch(c -> c.getConstraintType() == constraintType);
  }

  public static List<InlineConstraintDefinition> getEndOfBlockConstraints(final Syntax syntax) {
    return getInlineConstraintsByStrategy(syntax, InlineConstraintDefinitionStrategy.END_OF_BLOCK);
  }

  public static List<InlineConstraintDefinition> getEndOfLineConstraints(final Syntax syntax) {
    return getInlineConstraintsByStrategy(syntax, InlineConstraintDefinitionStrategy.END_OF_LINE);
  }

  private static List<InlineConstraintDefinition> getInlineConstraintsByStrategy(
      final Syntax syntax, final InlineConstraintDefinitionStrategy strategy) {
    return syntax.getTableCreation()
        .getTemplates()
        .getInlineConstraints()
        .getConstraint()
        .stream()
        .filter(
            inlineConstraintDefinition -> inlineConstraintDefinition
                .getStrategy()
                .equals(strategy)
        )
        .toList();
  }

  public static InlineConstraintDefinition getInlineConstraintDefinitionByType(final Syntax syntax,
      final ConstraintType constraintType) {
    return syntax.getTableCreation()
        .getTemplates()
        .getInlineConstraints()
        .getConstraint()
        .stream()
        .filter(
            inlineConstraintDefinition -> inlineConstraintDefinition
                .getConstraintType()
                .equals(constraintType)
        )
        .findFirst()
        .orElseThrow();
  }

}
