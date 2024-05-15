package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrimaryKeyConstraint extends Constraint {

  private final Table table;

  private final List<Column> columns;

}
