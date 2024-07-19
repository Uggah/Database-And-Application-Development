package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;

public interface ConstraintDefinitionBuilder<T extends Constraint> {

  /**
   * Creates a constraint definition statement for the given constraint.
   *
   * @param constraint The constraint
   * @return The constraint definition statement
   */
  String createConstraintDefinitionStatement(final T constraint);

}
