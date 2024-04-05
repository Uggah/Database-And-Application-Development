package de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel.Column;
import lombok.Data;

@Data
public abstract class Constraint {

  private ConstraintType constraintType;
  private Column[] columns;

}
