package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.Column;
import lombok.Data;

@Data
public abstract class Constraint {

  private ConstraintType constraintType;
  private Column[] columns;

}
