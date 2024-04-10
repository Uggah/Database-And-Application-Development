package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
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

    final List<Table> tables = retrieveTables();

    for (final Table table : tables) {
      final List<Column> columns = retrieveColumns(table);
      table.addColumns(columns);

      final List<Constraint> constraints = retrieveConstraints(
          table);
      table.addConstraints(constraints);
    }

    return log.exit(tables);
  }

  /**
   * Retrieves all non-system tables {@link Table}, that are neither
   * temporary nor views.
   *
   * @return an unmodifiable {@link List} of all retrieved {@link Table Tables}
   * @throws SQLException if a database access error occurs
   */
  protected List<Table> retrieveTables() throws SQLException {
    log.entry();

    final ResultSet tables = connection.getMetaData()
        .getTables(null, null, "%", new String[]{"TABLE"});

    final List<Table> outTables = new ArrayList<>();

    while (tables.next()) {
      final Table outTable = new Table(
          tables.getString("TABLE_SCHEM"),
          tables.getString("TABLE_NAME"),
          TableType.byTableTypeString(tables.getString("TABLE_TYPE"))
      );

      outTables.add(outTable);
    }

    return log.exit(Collections.unmodifiableList(outTables));
  }

  /**
   * Retrieves all {@link Column Columns} in the table {@link Table}.
   *
   * @param table {@link Table} to get the
   *                        {@link Column Columns} of.
   * @return an unmodifiable {@link List} of all {@link Column Columns} in the table {@link Table}
   * @throws SQLException if a database access error occurs
   */
  protected List<Column> retrieveColumns(final Table table)
      throws SQLException {
    log.entry(table);

    final ResultSet columns = connection.getMetaData().getColumns(null, table.getSchema(),
        table.getName(), "%");

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

  protected final List<Constraint> retrieveConstraints(
      final Table table) throws SQLException {
    log.entry(table);

    final List<Constraint> constraints = new ArrayList<>();

    constraints.add(retrievePrimaryKeyConstraint(table));
    constraints.addAll(retrieveForeignKeyConstraints(table));
    constraints.addAll(retrieveUniqueConstraints(table));

    return log.exit(Collections.unmodifiableList(constraints));
  }

  protected PrimaryKeyConstraint retrievePrimaryKeyConstraint(
      final Table table) throws SQLException {
    log.entry(table);

    final List<Column> columns = new ArrayList<>();

    final ResultSet resultSet = connection.getMetaData()
        .getPrimaryKeys(null, table.getSchema(), table.getName());

    while (resultSet.next()) {
      final String columnName = resultSet.getString("COLUMN_NAME");
    }

    return log.exit(null);
  }

  protected List<ForeignKeyConstraint> retrieveForeignKeyConstraints(
      final Table table) throws SQLException {
    log.entry(table);

    final ResultSet resultSet = connection.getMetaData().getCrossReference(
        null,
        null,
        null,
        null,
        table.getSchema(),
        table.getName()
    );

    return log.exit(Collections.emptyList());
  }

  protected List<UniqueConstraint> retrieveUniqueConstraints(final Table table)
      throws SQLException {
    log.entry(table);

    return log.exit(Collections.emptyList());
  }

}
