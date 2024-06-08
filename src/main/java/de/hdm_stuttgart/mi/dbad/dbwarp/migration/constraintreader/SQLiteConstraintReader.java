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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteConstraintReader extends AbstractConstraintReader implements AutoCloseable {

  private final PreparedStatement preparedStatementUniqueConstraints;
  private final PreparedStatement preparedStatementIndexInfo;

  public SQLiteConstraintReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);

    preparedStatementUniqueConstraints = this.connection.prepareStatement(
        "SELECT * FROM pragma_index_list(?) WHERE \"UNIQUE\"=1 AND PARTIAL=0");
    preparedStatementIndexInfo = this.connection.prepareStatement(
        "SELECT * FROM pragma_index_info(?)");

    log.exit();
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

  protected Collection<UniqueConstraint> retrieveUniqueConstraints(final Table table)
      throws SQLException {
    log.entry(table);

    preparedStatementUniqueConstraints.setString(1, table.getName());
    final ResultSet allIndexes = preparedStatementUniqueConstraints.executeQuery();

    final List<String> uniqueIndexes = new ArrayList<>();

    while (allIndexes.next()) {
      uniqueIndexes.add(allIndexes.getString("NAME"));
    }

    final Set<UniqueConstraint> constraints = new HashSet<>();

    for (String index : uniqueIndexes) {
      constraints.add(retrieveUniqueConstraintByIndexName(index, table));
    }

    return log.exit(Collections.unmodifiableSet(constraints));
  }

  private UniqueConstraint retrieveUniqueConstraintByIndexName(String indexName, Table table)
      throws SQLException {
    log.entry(indexName, table);

    final UniqueConstraint outConstraint = new UniqueConstraint(table, indexName);

    preparedStatementIndexInfo.setString(1, indexName);
    final ResultSet columns = preparedStatementIndexInfo.executeQuery();

    while (columns.next()) {
      outConstraint.addColumn(table.getColumnByName(columns.getString("NAME")));
    }

    return log.exit(outConstraint);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    preparedStatementIndexInfo.close();
    preparedStatementUniqueConstraints.close();
    log.exit();
  }
}
