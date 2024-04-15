package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteConstraintReader extends AbstractConstraintReader {

  public SQLiteConstraintReader(
      ConnectionManager connectionManager) {
    super(connectionManager);
  }

  @Override
  public List<Constraint> readConstraints(Table table) throws SQLException {
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

  private List<UniqueConstraint> retrieveUniqueConstraints(final Table table)
      throws SQLException {
    log.entry(table);

    final List<UniqueConstraint> constraints = new ArrayList<>();
    final ResultSet allIndexes = connection.createStatement()
        .executeQuery(String.format("PRAGMA index_list('%s')", table.getName()));
    final List<String> uniqueIndexes = new ArrayList<>();

    while (allIndexes.next()) {
      if (allIndexes.getBoolean("UNIQUE") && !allIndexes.getBoolean("PARTIAL")) {
        uniqueIndexes.add(allIndexes.getString("NAME"));
      }
    }

    for (String index : uniqueIndexes) {
      constraints.add(retriveUniqueConstraintByIndexName(index, table));
    }

    return constraints;
  }

  private UniqueConstraint retriveUniqueConstraintByIndexName(String indexName, Table table)
      throws SQLException {

    UniqueConstraint outConstraint = new UniqueConstraint();

    final ResultSet columns = connection.createStatement()
        .executeQuery(String.format("PRAGMA index_info('%s')", indexName));
    while (columns.next()) {
      outConstraint.addColumn(table.getColumnByName(columns.getString("NAME")));
    }

    return outConstraint;
  }
}
