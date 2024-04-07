package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ForeignKeyConstraint extends Constraint {

  /**
   * {@link TableDescriptor} of the table importing foreign columns.
   */
  private final TableDescriptor childTableDescriptor;

  /**
   * {@link TableDescriptor} of the table exporting primary key columns.
   */
  private final TableDescriptor parentTableDescriptor;

}
