package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints;

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
