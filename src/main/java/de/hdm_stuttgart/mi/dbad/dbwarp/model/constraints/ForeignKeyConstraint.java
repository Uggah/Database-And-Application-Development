package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ForeignKeyConstraint extends Constraint {

  /**
   * Name of the foreign key constraint.
   */
  private final String name;

  /**
   * {@link Table} of the table importing foreign columns.
   */
  private final Table childTable;

  /**
   * {@link Table} of the table exporting primary key columns.
   */
  private final Table parentTable;

  /**
   * {@link Column}s of the child table.
   */
  private final List<Column> childColumns = new ArrayList<>();

  /**
   * {@link Column}s of the parent table.
   */
  private final List<Column> parentColumns = new ArrayList<>();

}
