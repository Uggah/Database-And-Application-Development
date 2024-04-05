package de.hdm_stuttgart.mi.dbad.dbwarp.model;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Table {

  private final String name;
  private final List<Column> columns = new ArrayList<>();
  private final List<Constraint> constraints = new ArrayList<>();

  public void addColumn(Column column) {
    columns.add(column);
  }

}
