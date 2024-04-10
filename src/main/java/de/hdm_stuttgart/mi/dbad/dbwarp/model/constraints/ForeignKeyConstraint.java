package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ForeignKeyConstraint extends Constraint {

  /**
   * {@link Table} of the table importing foreign columns.
   */
  private final Table childTable;

  /**
   * {@link Table} of the table exporting primary key columns.
   */
  private final Table parentTable;

}
