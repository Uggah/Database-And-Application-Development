package de.hdm_stuttgart.mi.dbad.dbwarp.model.column;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.JDBCType;
import lombok.Data;
import lombok.ToString;

@Data
public class Column {

  /**
   * The {@link Column Column's} {@link Table}
   */
  @ToString.Exclude
  private final Table table;

  /**
   * The {@link Column Column's} name
   */
  private final String name;

  /**
   * The {@link Column Column's} {@link JDBCType}
   */
  private final JDBCType type;

  /**
   * If the {@link Column} is nullable. Is {@code true} if it is definitely nullable, {@code false}
   * if it <i>might</i> not be nullable. Could also be {@code null} if nullability could not be
   * determined.
   */
  private final Boolean nullable;

  /**
   * Size of the column
   */
  private final int size;

  /**
   * Default value. Is {@code null} if no default value is set.
   */
  private Object defaultValue = null;

}
