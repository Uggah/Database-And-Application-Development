package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

/*-
 * #%L
 * DBWarp
 * %%
 * Copyright (C) 2024 Kay Knöpfle, Lucca Greschner and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * Implements a generic approach to read constraints from SQL databases.
 * This class is intended to be extended by database specific implementations.
 * The class provides default implementations for reading primary keys and foreign keys through JDBC Metadata.
 */
@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractConstraintReader implements ConstraintReader {

  protected final Connection connection;

  protected AbstractConstraintReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  /**
   * Template method for reading constraints from a {@link Table}. Calls the
   * {@link this#retrievePrimaryKeyConstraint(List)},
   * {@link this#retrieveForeignKeyConstraints(List)} and
   * {@link this#retrieveUniqueConstraints(List)} methods that need to be implemented by child
   * classes.
   *
   * @param tableList List of {@link Table tables} to read {@link de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint constraints} from.
   * @throws SQLException if an SQL error occurs.
   */
  @Override
  public final void readConstraints(final List<Table> tableList) throws SQLException {
    log.entry(tableList);

    retrievePrimaryKeyConstraint(tableList);
    retrieveUniqueConstraints(tableList);
    retrieveForeignKeyConstraints(tableList);

    log.exit(tableList);
  }

  /**
   * Default implementation for retrieving primary keys from a {@link Table}. The primary key is
   * added to the {@link Table} object. This method can be overriden by child classes to implement
   * database specific behavior.
   *
   * @param tableList List of {@link Table tables} to retrieve the primary key constraints for.
   * @throws SQLException if an SQL error occurs while retrieving primary key constraints.
   */
  protected void retrievePrimaryKeyConstraint(final List<Table> tableList) throws SQLException {
    log.entry(tableList);

    for (Table table : tableList) {

      final List<Column> columns = new ArrayList<>();

      final ResultSet resultSet = connection.getMetaData()
          .getPrimaryKeys(
              connection.getCatalog(),
              table.getSchema(),
              table.getName()
          );

      if (!resultSet.next()) {
        continue;
      }

      final String name = resultSet.getString("PK_NAME");

      do {
        final String columnName = resultSet.getString("COLUMN_NAME");
        columns.add(table.getColumnByName(columnName));
      } while (resultSet.next());

      table.setPrimaryKeyConstraint(new PrimaryKeyConstraint(name, table, columns));
    }

    log.exit();
  }

  /**
   * Default implementation for retrieving foreign key constraints from a {@link Table}. The foreign
   * key constraints are added to the {@link Table} object. This method can be overriden by child
   * classes to implement database specific behavior.
   *
   * @param tableList List of {@link Table tables} to retrieve the foreign key constraints for.
   * @throws SQLException if an SQL error occurs while retrieving foreign key constraints.
   */
  protected void retrieveForeignKeyConstraints(final List<Table> tableList) throws SQLException {
    log.entry(tableList);

    for (final Table table : tableList) {
      final Map<String, ForeignKeyConstraint> constraints = new HashMap<>();

      final ResultSet resultSet = connection.getMetaData().getImportedKeys(
          connection.getCatalog(),
          table.getSchema(),
          table.getName()
      );

      while (resultSet.next()) {

        final String fkName = resultSet.getString("FK_NAME");
        final String parentTableName = resultSet.getString("PKTABLE_NAME");

        final Table parentTable = tableList.stream()
            .filter(t -> t.getName().equals(parentTableName))
            .findFirst()
            .orElseThrow();

        if (!constraints.containsKey(fkName)) {
          constraints.put(fkName, new ForeignKeyConstraint(fkName, table, parentTable));
        }

        final Column childColumn = table.getColumnByName(resultSet.getString("FKCOLUMN_NAME"));
        final Column parentColumn = parentTable.getColumnByName(
            resultSet.getString("PKCOLUMN_NAME"));
        constraints.get(fkName).getChildColumns().add(childColumn);
        constraints.get(fkName).getParentColumns().add(parentColumn);

      }

      table.addForeignKeyConstraints(constraints.values());
    }

    log.exit();
  }

  /**
   * Default implementation for retrieving unique constraints from a {@link Table}. The unique
   * constraints are added to the {@link Table} object. This method can be overriden by child
   * classes to implement database specific behavior.
   *
   * @param tableList List of {@link Table tables} to retrieve the unique constraints for.
   * @throws SQLException if an SQL error occurs while retrieving unique constraints.
   */
  protected void retrieveUniqueConstraints(List<Table> tableList) throws SQLException {
    log.entry(tableList);

    for (final Table table : tableList) {
      final ResultSet indexResultSet = connection.getMetaData().getIndexInfo(
          connection.getCatalog(),
          table.getSchema(),
          table.getName(),
          true,
          false
      );

      final Map<String, UniqueConstraint> uniqueConstraints = new HashMap<>();

      while (indexResultSet.next()) {
        final String indexName = indexResultSet.getString("INDEX_NAME");
        final Column column = table.getColumnByName(indexResultSet.getString("COLUMN_NAME"));

        if (table.getPrimaryKeyConstraint() != null && indexName.equals(
            table.getPrimaryKeyConstraint().getName())) {
          continue;
        }

        uniqueConstraints.compute(indexName, (k, v) -> {
          if (v == null) {
            v = new UniqueConstraint(indexName, table);
          }

          v.addColumn(column);

          return v;
        });
      }

      table.addUniqueConstraints(uniqueConstraints.values());
    }

    log.exit();
  }

}
