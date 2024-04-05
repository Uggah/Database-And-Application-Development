package de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel.constraints.Constraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Table {

  final private String name;
  private ConnectionManager connectionManager;
  private List<Column> columns = new ArrayList<>();
  private Constraint[] constraints;

  public void addColumn(Column column) {
    columns.add(column);
  }

}
