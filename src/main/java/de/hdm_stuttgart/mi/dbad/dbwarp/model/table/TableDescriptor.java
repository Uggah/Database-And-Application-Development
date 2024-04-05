package de.hdm_stuttgart.mi.dbad.dbwarp.model.table;

import lombok.Data;

@Data
public class TableDescriptor {

  private final String schema;
  private final String name;
  private final TableType type;

}
