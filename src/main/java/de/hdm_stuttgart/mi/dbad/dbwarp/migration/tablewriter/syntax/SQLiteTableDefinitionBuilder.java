package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@RequiredArgsConstructor
public class SQLiteTableDefinitionBuilder {

  private static final String CREATE_TABLE = "CREATE TABLE %s (";

  private static final String COLUMN_DEFINITION = "%s %s%s,";

  private static final String NOT_NULL = " NOT NULL";

  private static final String PRIMARY_KEY = "PRIMARY KEY (%s),";
  private static final String FOREIGN_KEY = "FOREIGN KEY %s REFERENCES %s (%s),";
  private static final String UNIQUE_CONSTRAINT = "UNIQUE (%s),";

  private static final String END_CREATE_TABLE = ");";

  public String createTableDefinitionStatement(final Table table) {
    log.entry(table);

    final StringBuilder sb = new StringBuilder();

    sb.append(String.format(CREATE_TABLE, table.getFullName()));

    for (final Column column : table.getColumns()) {
      sb.append(createColumnDefinition(column));
    }

    for (final Constraint constraint : table.getConstraints()) {
      sb.append(createConstraintDefinition(constraint));
    }

    sb.deleteCharAt(sb.length() - 1);
    sb.append(END_CREATE_TABLE);

    return log.exit(sb.toString());
  }

  private String createColumnDefinition(final Column column) {
    log.entry(column);

    final String nullability = Boolean.FALSE.equals(column.getNullable()) ? NOT_NULL : "";

    return log.exit(String.format(COLUMN_DEFINITION, column.getName(),
        String.format("%s(%d)", column.getType().getName(), column.getSize()), nullability));
  }

  private String createConstraintDefinition(final Constraint constraint) {
    log.entry(constraint);

    if (constraint instanceof PrimaryKeyConstraint primaryKeyConstraint) {
      return log.exit(createPrimaryKeyConstraintDefinition(primaryKeyConstraint));
    }

    if (constraint instanceof UniqueConstraint uniqueConstraint) {
      return log.exit(createUniqueKeyConstraintDefinition(uniqueConstraint));
    }

    log.warn("Cannot migrate constraint to SQLite database: {}", constraint.toString());

    return log.exit("");
  }

  private String createPrimaryKeyConstraintDefinition(final PrimaryKeyConstraint constraint) {
    log.entry(constraint);

    final String columns = constraint.getColumns().stream()
        .map(Column::getName)
        .collect(Collectors.joining(","));

    return log.exit(String.format(PRIMARY_KEY, columns));
  }

  private String createUniqueKeyConstraintDefinition(final UniqueConstraint constraint) {
    log.entry(constraint);

    final String columns = constraint.getColumns().stream()
        .map(Column::getName)
        .collect(Collectors.joining(","));

    return log.exit(String.format(UNIQUE_CONSTRAINT, columns));
  }

}
