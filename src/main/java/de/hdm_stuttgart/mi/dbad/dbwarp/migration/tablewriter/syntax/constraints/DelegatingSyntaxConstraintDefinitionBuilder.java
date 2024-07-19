package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.exception.SyntaxRenderingException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import java.util.Map;

/**
 * The facade for the constraint definition builders.
 */
public class DelegatingSyntaxConstraintDefinitionBuilder implements
    ConstraintDefinitionBuilder<Constraint> {

  private final Map<Class<? extends Constraint>, ConstraintDefinitionBuilder<? extends Constraint>> constraintDefinitionBuilderMap;

  /**
   * Creates a new instance of the facade.
   *
   * @param syntax The syntax
   */
  public DelegatingSyntaxConstraintDefinitionBuilder(final Syntax syntax) {
    this.constraintDefinitionBuilderMap = Map.of(
        PrimaryKeyConstraint.class, new SyntaxPrimaryKeyConstraintDefinitionBuilder(syntax),
        ForeignKeyConstraint.class, new SyntaxForeignKeyConstraintDefinitionBuilder(syntax),
        UniqueConstraint.class, new SyntaxUniqueConstraintDefinitionBuilder(syntax)
    );
  }

  /**
   * Creates a constraint definition statement for the given constraint.
   *
   * @param constraint The constraint
   * @return The constraint definition statement
   */
  @Override
  public String createConstraintDefinitionStatement(final Constraint constraint) {
    if (constraint instanceof PrimaryKeyConstraint primaryKeyConstraint) {
      final SyntaxPrimaryKeyConstraintDefinitionBuilder constraintDefinitionBuilder = (SyntaxPrimaryKeyConstraintDefinitionBuilder) constraintDefinitionBuilderMap.get(
          PrimaryKeyConstraint.class);

      return constraintDefinitionBuilder.createConstraintDefinitionStatement(primaryKeyConstraint);
    }

    if (constraint instanceof ForeignKeyConstraint foreignKeyConstraint) {
      final SyntaxForeignKeyConstraintDefinitionBuilder constraintDefinitionBuilder = (SyntaxForeignKeyConstraintDefinitionBuilder) constraintDefinitionBuilderMap.get(
          ForeignKeyConstraint.class);

      return constraintDefinitionBuilder.createConstraintDefinitionStatement(foreignKeyConstraint);
    }

    if (constraint instanceof UniqueConstraint uniqueConstraint) {
      final SyntaxUniqueConstraintDefinitionBuilder constraintDefinitionBuilder = (SyntaxUniqueConstraintDefinitionBuilder) constraintDefinitionBuilderMap.get(
          UniqueConstraint.class);

      return constraintDefinitionBuilder.createConstraintDefinitionStatement(uniqueConstraint);
    }

    throw new SyntaxRenderingException(
        "Unsupported constraint type: " + constraint.getClass().getSimpleName());
  }
}
