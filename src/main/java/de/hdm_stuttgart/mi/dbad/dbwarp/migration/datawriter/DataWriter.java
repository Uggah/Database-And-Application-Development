package de.hdm_stuttgart.mi.dbad.dbwarp.migration.datawriter;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;

public interface DataWriter {

  void transferData(final Table table) throws Exception;

}
