package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.Column;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Constraint {

  private final ConstraintType constraintType;
  private final List<Column> columns = new ArrayList<>();

  public Constraint(ConstraintType constraintType, Column[] columns) {
    this.constraintType = constraintType;
    Collections.addAll(this.columns, columns);
  }

  public void addColumn(Column column) {
    columns.add(column);
  }

}
