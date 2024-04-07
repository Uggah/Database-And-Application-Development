package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableDescriptor;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public abstract class DefaultTableReader implements TableReader {

  protected final Connection connection;

  protected DefaultTableReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  @Override
  public List<Table> readTables() throws SQLException {
    log.entry();

    final List<Table> tables = retrieveTableDescriptors().stream().map(Table::new).toList();

    for (final Table table : tables) {
      final List<Column> columns = retrieveColumnsByTableDescriptor(table.getDescriptor());
      table.addColumns(columns);

      final List<Constraint> constraints = retrieveConstraintsByTable(table);
      table.addConstraints(constraints);
    }

    return log.exit(tables);
  }

  /**
   * Retrieves {@link TableDescriptor descriptors} of all non-system tables, that are neither
   * temporary nor views.
   *
   * @return an unmodifiable {@link List} of all retrieved {@link TableDescriptor TableDescriptors}
   * @throws SQLException if a database access error occurs
   */
  protected List<TableDescriptor> retrieveTableDescriptors() throws SQLException {
    log.entry();

    final ResultSet tables = connection.getMetaData()
        .getTables(null, null, "%", new String[]{"TABLE"});

    final List<TableDescriptor> tableDescriptors = new ArrayList<>();

    while (tables.next()) {
      final TableDescriptor tableDescriptor = new TableDescriptor(
          tables.getString("TABLE_SCHEM"),
          tables.getString("TABLE_NAME"),
          TableType.byTableTypeString(tables.getString("TABLE_TYPE"))
      );

      tableDescriptors.add(tableDescriptor);
    }

    return log.exit(Collections.unmodifiableList(tableDescriptors));
  }

  /**
   * Retrieves all {@link Column Columns} in the table described by the given
   * {@link TableDescriptor}.
   *
   * @param tableDescriptor {@link TableDescriptor} of the {@link Table} to get the
   *                        {@link Column Columns} of.
   * @return an unmodifiable {@link List} of all {@link Column Columns} in the table described by
   * the given {@link TableDescriptor}
   * @throws SQLException if a database access error occurs
   */
  protected List<Column> retrieveColumnsByTableDescriptor(final TableDescriptor tableDescriptor)
      throws SQLException {
    log.entry(tableDescriptor);

    final ResultSet columns = connection.getMetaData().getColumns(null, tableDescriptor.getSchema(),
        tableDescriptor.getName(), "%");

    final List<Column> columnList = new ArrayList<>();

    while (columns.next()) {
      columnList.add(readColumn(columns));
    }

    return log.exit(Collections.unmodifiableList(columnList));
  }

  /**
   * Reads a single {@link Column}. The given {@link ResultSet} must have been retrieved from
   * {@link java.sql.DatabaseMetaData#getColumns(String, String, String, String)} and point to the
   * desired row.
   *
   * @param resultSet the {@link ResultSet} to get the {@link Column Column's} metadata from.
   * @return The {@link Column}
   * @throws SQLException if a database access error occurs
   */
  protected Column readColumn(final ResultSet resultSet) throws SQLException {
    final String name = resultSet.getString("COLUMN_NAME");
    final int type = resultSet.getInt("DATA_TYPE");

    final int nullability = resultSet.getInt("IS_NULLABLE");
    final int size = resultSet.getInt("COLUMN_SIZE");

    final Boolean nullable;

    switch (nullability) {
      case 0 -> nullable = false;
      case 1 -> nullable = true;
      case 2 -> nullable = null;
      default -> throw new IllegalArgumentException(
          String.format("Unknown nullability: %s", nullability));
    }

    return new Column(
        name,
        JDBCType.valueOf(type),
        nullable,
        size
    );
  }

  /**
   * Retrieves all {@link Constraint Constraints} in the table described by the given
   * {@link TableDescriptor}.
   *
   * @param table {@link Table} of the {@link Table} to get the {@link Constraint Constraints} of.
   * @return an unmodifiable {@link List} of all {@link Constraint Constraints} in the table
   * described by the given {@link Table}
   * @throws SQLException if a database access error occurs
   */
  protected List<Constraint> retrieveConstraintsByTable(final Table table) throws SQLException {
    return null;
  }

}
