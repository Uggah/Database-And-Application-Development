package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader.ColumnReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader.ConstraintReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
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

      final List<Constraint> constraints = constraintReader.readConstraints(
          table);
      table.addConstraints(constraints);
    }

    return log.exit(tables);
  }

  protected List<UniqueConstraint> retrieveUniqueConstraints(final Table table)
      throws SQLException {
    log.entry(table);

    return log.exit(Collections.emptyList());
  }

}
