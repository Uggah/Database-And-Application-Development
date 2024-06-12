package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
      columns.add(table.getColumnByName(columnName));
    }

    final String name = resultSet.getString("PK_NAME");

    return log.exit(new PrimaryKeyConstraint(name, table, columns));
  }

  protected List<ForeignKeyConstraint> retrieveForeignKeyConstraints(
      final Table table, final List<Table> tableList) throws SQLException {
    log.entry(table);

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
      final Column parentColumn = parentTable.getColumnByName(resultSet.getString("PKCOLUMN_NAME"));
      constraints.get(fkName).getChildColumns().add(childColumn);
      constraints.get(fkName).getParentColumns().add(parentColumn);


    }

    return log.exit(new ArrayList<>(constraints.values()));
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
