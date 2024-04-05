package de.hdm_stuttgart.mi.dbad.dbwarp.model;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Table {

  final private String name;
  final private List<Column> columns = new ArrayList<>();
  private List<Constraint> constraints = new ArrayList<>();

  public void addColumn(Column column) {
    columns.add(column);
  }

}
