package de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import org.junit.jupiter.api.Test;

class PrimaryKeyConstraintTest {

  @Test
  void testGetName() {
    final PrimaryKeyConstraint primaryKeyConstraint = new PrimaryKeyConstraint("name", null);

    assertEquals("name", primaryKeyConstraint.getName());
  }

  @Test
  void testGetNameWithNullName() {
    final Table table = new Table(null, "table", TableType.TABLE);

    final PrimaryKeyConstraint primaryKeyConstraint = new PrimaryKeyConstraint(null, table);

    assertEquals("PK_table", primaryKeyConstraint.getName());
  }

}