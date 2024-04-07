package de.hdm_stuttgart.mi.dbad.dbwarp.model.table;

import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.XSlf4j;

/**
 * This is the internal representation of a database table. It is used as an intermediate format for
 * the migration process.
 */
@Data
@XSlf4j
public class Table {

  private final TableDescriptor descriptor;

  private final List<Column> columns = new ArrayList<>();
  private final List<Constraint> constraints = new ArrayList<>();

  /**
   * Adds a {@link Column} to the model.
   *
   * @param column {@link Column} to add
   */
  public void addColumn(Column column) {
    log.entry(column);
    columns.add(column);
    log.exit();
  }

  /**
   * Adds multiple {@link Column Columns} to the model.
   *
   * @param columns {@link Iterable} of {@link Column Columns} to add
   */
  public void addColumns(Iterable<Column> columns) {
    log.entry(columns);
    columns.forEach(this::addColumn);
    log.exit();
  }

  /**
   * Adds a {@link Constraint Constraint} to the model.
   *
   * @param constraint {@link Iterable} of {@link Constraint Constraints} to add
   */
  public void addConstraint(Constraint constraint) {
    log.entry(constraint);
    constraints.add(constraint);
    log.exit();
  }

  /**
   * Adds multiple {@link Constraint Constraints} to the model.
   *
   * @param constraints {@link Iterable} of {@link Constraint Constraints} to add
   */
  public void addConstraints(Iterable<Constraint> constraints) {
    log.entry(constraints);
    constraints.forEach(this::addConstraint);
    log.exit();
  }

  public Column getColumnByName(String name) {
    return columns.parallelStream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
  }

}
