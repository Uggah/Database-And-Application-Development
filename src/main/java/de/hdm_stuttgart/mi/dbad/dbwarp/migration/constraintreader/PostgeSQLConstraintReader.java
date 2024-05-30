package de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.io.StringReader;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class PostgeSQLConstraintReader extends AbstractConstraintReader implements AutoCloseable {

  private final PreparedStatement preparedStatementUniqueConstraints;

  public PostgeSQLConstraintReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);

    preparedStatementUniqueConstraints = this.connection.prepareStatement(
        """
            SELECT
              at.attname AS column_name,
              cn.conname AS constraint_name
              FROM pg_catalog.pg_constraint cn
              LEFT JOIN pg_catalog.pg_attribute at ON at.attrelid = cn.conrelid AND at.attnum = ANY(cn.conkey)
                WHERE cn.conrelid = (SELECT oid FROM pg_catalog.pg_class WHERE relname LIKE ?)
                AND cn.contype = 'u';
            """
    );

    log.exit();
  }

  @Override
  public List<Constraint> readConstraints(Table table) throws SQLException {
    log.entry(table);
    return log.exit(Collections.emptyList());
  }

  @Override
  protected PrimaryKeyConstraint retrievePrimaryKeyConstraint(Table table) throws SQLException {
    log.entry();
    return log.exit(null);
  }

  @Override
  protected Collection<ForeignKeyConstraint> retrieveForeignKeyConstraints(Table table)
      throws SQLException {
    log.entry();
    return log.exit(Collections.emptyList());
  }

  @Override
  protected Collection<UniqueConstraint> retrieveUniqueConstraints(Table table)
      throws SQLException {
    log.entry();

    preparedStatementUniqueConstraints.setString(1, table.getName());
    final ResultSet allUniqueConstraints = preparedStatementUniqueConstraints.executeQuery();
    final List<UniqueConstraint> constraints = new ArrayList<>();

    while (allUniqueConstraints.next()) {
      final String constraintName = allUniqueConstraints.getString("constraint_name");
      final String columnName = allUniqueConstraints.getString("column_name");

      final Optional<UniqueConstraint> existingConstraintOptional = constraints.stream()
          .filter(constraint -> constraint.getName().equals(constraintName))
          .findAny();

      existingConstraintOptional.ifPresentOrElse(
          constraint -> constraint.addColumn(table.getColumnByName(columnName)),
          () -> {
            final UniqueConstraint constraint = new UniqueConstraint(
                table,
                constraintName
            );

            constraint.addColumn(
                table.getColumnByName(columnName)
            );

            constraints.add(constraint);
          }
      );
    }

    return log.exit(constraints);
  }

  @Override
  public void close() throws Exception {
    log.entry();
    this.preparedStatementUniqueConstraints.close();
    log.exit();
  }
}
