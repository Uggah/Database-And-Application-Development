package de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel;

import java.sql.JDBCType;
import lombok.Data;

@Data
public class Column {

  private final String name;
  private final JDBCType type;
}
