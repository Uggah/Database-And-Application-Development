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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.XSlf4j;

/**
 * SQLite specific {@link ConstraintReader}.
 */
@XSlf4j
public class SQLiteConstraintReader extends AbstractConstraintReader implements AutoCloseable {

  /**
   * {@link PreparedStatement} used to query unique constraints from the SQLite database.
   */
  private final PreparedStatement preparedStatementUniqueConstraints;
  /**
   * {@link PreparedStatement} used to query info about a specific index from the SQLite database.
   */
  private final PreparedStatement preparedStatementIndexInfo;

  public SQLiteConstraintReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);

    preparedStatementUniqueConstraints = this.connection.prepareStatement(
        "SELECT * FROM pragma_index_list(?) WHERE \"UNIQUE\"=1 AND ORIGIN='u' AND PARTIAL=0");
    preparedStatementIndexInfo = this.connection.prepareStatement(
        "SELECT * FROM pragma_index_info(?)");

    log.exit();
  }

  @Override
  protected void retrievePrimaryKeyConstraint(
      final List<Table> tableList) throws SQLException {
    log.entry(tableList);

    for (Table table : tableList) {

      final List<Column> columns = new ArrayList<>();

      final ResultSet resultSet = connection.getMetaData()
          .getPrimaryKeys(null, table.getSchema(), table.getName());

      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        columns.add(table.getColumnByName(columnName));
      }

      final String name = resultSet.getString("PK_NAME");

      table.addConstraint(new PrimaryKeyConstraint(name, table, columns));
    }

  }

  @Override
  protected void retrieveForeignKeyConstraints(final List<Table> tableList) throws SQLException {
    log.entry(tableList);

    for (Table table : tableList) {
      final Map<String, ForeignKeyConstraint> constraints = new HashMap<>();

      final ResultSet resultSet = connection.getMetaData().getImportedKeys(
          null,
          table.getSchema(),
          table.getName()
      );

      while (resultSet.next()) {

        final String fkName = resultSet.getString("FK_NAME");
        final String parentTableName = resultSet.getString("PKTABLE_NAME");

        final Table parentTable = tableList.stream()
            .filter(t -> t.getName().equals(parentTableName))
            .findFirst()
            .orElse(null);

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
  }

  /**
   * Retrieves all {@link UniqueConstraint UniqueConstraints} in the given
   * {@link Table}.
   * The given List of {@link Table tables} will be modified in place to contain the retrieved constraints.
   *
   * @param tableList List of {@link Table tables} to retrieve the {@link UniqueConstraint UniqueConstraints} for.
   * @throws SQLException if an SQL error occurs while retrieving
   *                      {@link UniqueConstraint UniqueConstraints}.
   */
  @Override
  protected void retrieveUniqueConstraints(final List<Table> tableList) throws SQLException {
    log.entry(tableList);

    for (Table table : tableList) {
      preparedStatementUniqueConstraints.setString(1, table.getName());
      final ResultSet allIndexes = preparedStatementUniqueConstraints.executeQuery();

      final List<String> uniqueIndexes = new ArrayList<>();

      while (allIndexes.next()) {
        uniqueIndexes.add(allIndexes.getString("NAME"));
      }

      final Set<Constraint> constraints = new HashSet<>();

      for (String index : uniqueIndexes) {
        constraints.add(retrieveUniqueConstraintByIndexName(index, table));
      }

      table.addConstraints(constraints);
    }
  }

  /**
   * Retrieves a single {@link UniqueConstraint} from the given {@link Table} by its name.
   *
   * @param indexName name of the {@link UniqueConstraint}.
   * @param table     {@link Table} to get the {@link UniqueConstraint} from.
   * @return the wanted {@link UniqueConstraint}
   * @throws SQLException when an SQL error occurs when querying for the {@link UniqueConstraint}.
   *                      For example, if the {@link UniqueConstraint} does not exist.
   */
  private UniqueConstraint retrieveUniqueConstraintByIndexName(String indexName, Table table)
      throws SQLException {
    log.entry(indexName, table);

    final UniqueConstraint outConstraint = new UniqueConstraint(table, indexName);

    preparedStatementIndexInfo.setString(1, indexName);
    final ResultSet columns = preparedStatementIndexInfo.executeQuery();

    while (columns.next()) {
      outConstraint.getColumns().add(table.getColumnByName(columns.getString("NAME")));
    }

    return log.exit(outConstraint);
  }

  /**
   * Closes the {@link PreparedStatement} initialized during construction of this {@link ConstraintReader}.
   */
  @Override
  public void close() throws Exception {
    log.entry();
    preparedStatementIndexInfo.close();
    preparedStatementUniqueConstraints.close();
    log.exit();
  }
}
