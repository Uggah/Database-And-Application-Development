package de.hdm_stuttgart.mi.dbad.dbwarp.model;

import java.sql.JDBCType;
import lombok.Data;

@Data
public class Column {

  private final String name;
  private final JDBCType type;

}
