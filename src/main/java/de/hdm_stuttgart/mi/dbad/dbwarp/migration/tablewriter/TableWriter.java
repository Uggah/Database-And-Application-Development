package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;

public interface TableWriter {

  void writeTable(Table table) throws Exception;

}
