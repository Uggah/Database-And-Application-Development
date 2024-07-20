package de.hdm_stuttgart.mi.dbad.dbwarp.syntax;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintDefinitionStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;

public class SyntaxHelper {

  private SyntaxHelper() {
  }

  public static ConstraintDefinitionStrategy getPrimaryKeyStrategy(final Syntax syntax) {
    return syntax.getTemplates().getPrimaryKeyConstraint().getStrategy();
  }

  public static ConstraintDefinitionStrategy getForeignKeyStrategy(final Syntax syntax) {
    return syntax.getTemplates().getForeignKeyConstraint().getStrategy();
  }

  public static ConstraintDefinitionStrategy getNotNullStrategy(final Syntax syntax) {
    return syntax.getTemplates().getNotNullConstraint().getStrategy();
  }

  public static ConstraintDefinitionStrategy getUniqueStrategy(final Syntax syntax) {
    return syntax.getTemplates().getUniqueConstraint().getStrategy();
  }

}
