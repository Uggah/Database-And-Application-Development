package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public abstract class AbstractJDBCTableWriter implements TableWriter {

  protected final Connection connection;

  protected AbstractJDBCTableWriter(final ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getTargetDatabaseConnection();
    log.exit(this);
  }

  @Override
  public void writeTable(final Table table) throws Exception {
    log.entry(table);
    this.writeTableDefinition(table);
    this.writeTableConstraints(table);
    log.exit();
  }

  public abstract void writeTableDefinition(final Table table) throws Exception;

  public abstract void writeTableConstraints(final Table table) throws Exception;
}
