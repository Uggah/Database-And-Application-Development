package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UniqueConstraint extends Constraint {

  private final List<Column> columns = new ArrayList<>();

  public void addColumn(Column column) {
    columns.add(column);
  }

}
