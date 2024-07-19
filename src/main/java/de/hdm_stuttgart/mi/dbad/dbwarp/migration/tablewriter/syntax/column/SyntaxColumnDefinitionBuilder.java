package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.exception.SyntaxRenderingException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.InlineConstraintDefinition;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL column definition statements based on a specific syntax. This
 * class is responsible for constructing the SQL statement for a column definition, including any
 * inline constraints, according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxColumnDefinitionBuilder implements ColumnDefinitionBuilder {

  private final Syntax syntax;
  private final ConstraintDefinitionBuilder<Constraint> constraintDefinitionBuilder;

  /**
   * Generates a SQL column definition statement for a given column. This method takes into account
   * the column's properties and any applicable constraints, constructing a statement that is
   * compliant with the provided syntax.
   *
   * @param column The column for which to generate the definition statement.
   * @return A SQL statement string that defines the column within a table.
   */
  @Override
  public String createColumnDefinitionStatement(Column column) {
    log.entry(column);

    final List<InlineConstraintDefinition> endOfLineConstraintDefinitions = SyntaxHelper.getEndOfLineConstraints(
        syntax);
    final List<String> endOfLineConstraints = new ArrayList<>();

    endOfLineConstraintDefinitions.forEach(constraintDefinition -> {
      switch (constraintDefinition.getConstraintType()) {
        case PRIMARY_KEY -> {
          if (column.getPrimaryKey() == null) {
            return;
          }

          endOfLineConstraints.add(constraintDefinitionBuilder.createConstraintDefinitionStatement(
              column.getPrimaryKey()));
        }
        case FOREIGN_KEY -> {
          final List<ForeignKeyConstraint> foreignKeyConstraints = column.getReferencingForeignKeys();

          foreignKeyConstraints.forEach(foreignKey ->
              endOfLineConstraints.add(
                  constraintDefinitionBuilder.createConstraintDefinitionStatement(foreignKey)
              )
          );
        }
        case NOT_NULL -> {
          if (!Boolean.FALSE.equals(column.getNullable())) {
            return;
          }

          endOfLineConstraints.add(constraintDefinition.getValue());
        }
        default -> throw new SyntaxRenderingException(
            "Unsupported end-of-line constraint type: " + constraintDefinition.getConstraintType());
      }
    });

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.COLUMN_NAME, column.getName());
    params.put(SyntaxPlaceholders.COLUMN_TYPE,
        column.getType().getName()); // TODO: Implement configurable type mappings!
    params.put(SyntaxPlaceholders.END_OF_LINE_CONSTRAINTS, String.join(" ", endOfLineConstraints));

    final String out = StringSubstitutor.replace(
        syntax.getTableCreation().getTemplates().getColumnDefinition(), params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END);

    return log.exit(out);
  }
}
