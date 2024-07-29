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
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class MariaDBConstraintReader extends AbstractConstraintReader {

  public MariaDBConstraintReader(ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  @Override
  protected void retrievePrimaryKeyConstraint(final List<Table> tableList) throws SQLException {
    super.retrievePrimaryKeyConstraint(tableList);
    tableList.parallelStream().filter(table -> table.getPrimaryKeyConstraint() != null)
        .forEach(table -> {
          if (Objects.equals(table.getPrimaryKeyConstraint().getName(), "PRIMARY")) {
            table.getPrimaryKeyConstraint().setName(null);
          }
        });
  }

  @Override
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
        String indexName = indexResultSet.getString("INDEX_NAME");
        if (indexName.equals("PRIMARY")) {
          continue;
        }
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

    log.exit(tableList);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }
}
