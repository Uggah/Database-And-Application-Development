package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader.ColumnReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader.ConstraintReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public abstract class AbstractJDBCSchemaReader implements SchemaReader {

  protected final Connection connection;
  protected final TableReader tableReader;
  protected final ColumnReader columnReader;
  protected final ConstraintReader constraintReader;

  protected AbstractJDBCSchemaReader(
      ConnectionManager connectionManager,
      TableReader tableReader,
      ColumnReader columnReader,
      ConstraintReader constraintReader
  ) {
    log.entry(connectionManager, tableReader, columnReader);
    this.connection = connectionManager.getSourceDatabaseConnection();
    this.tableReader = tableReader;
    this.columnReader = columnReader;
    this.constraintReader = constraintReader;
    log.exit();
  }

  @Override
  public List<Table> readSchema() throws SQLException {
    log.entry();

    final List<Table> tables = tableReader.readTables();

    for (final Table table : tables) {
      final List<Column> columns = columnReader.readColumns(table);
      table.addColumns(columns);
    }

    constraintReader.readConstraints(tables);

    return log.exit(tables);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    tableReader.close();
    columnReader.close();
    constraintReader.close();
    log.exit();
  }

}
