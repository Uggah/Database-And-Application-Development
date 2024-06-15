package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
public class UniqueConstraint extends Constraint {

  @ToString.Exclude
  private final Table table;

  private final String name;

  private final List<Column> columns = new ArrayList<>();

  public void addColumn(Column column) {
    columns.add(column);
  }

}
