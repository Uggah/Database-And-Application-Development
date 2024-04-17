package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
