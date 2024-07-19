package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.table;

import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_BEGIN;
import static de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders.PLACEHOLDER_END;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.TableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.exception.SyntaxRenderingException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.InlineConstraintDefinition;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

/**
 * Builder class for generating SQL table definition statements based on a specific syntax. This
 * class is responsible for constructing the SQL statement for a table definition, including any
 * constraints, according to the syntax rules provided.
 */
@XSlf4j
@RequiredArgsConstructor
public class SyntaxTableDefinitionBuilder implements TableDefinitionBuilder {

  private final Syntax syntax;
  private final ColumnDefinitionBuilder columnDefinitionBuilder;
  private final ConstraintDefinitionBuilder<Constraint> constraintDefinitionBuilder;

  /**
   * Generates a SQL table definition statement for a given table. This method takes into account
   * the table's properties and any applicable constraints, constructing a statement that is
   * compliant with the provided syntax.
   *
   * @param table The table for which to generate the definition statement.
   * @return A SQL statement string that defines the table.
   */
  @Override
  public String createTableDefinitionStatement(Table table) {
    log.entry(table);

    final Map<String, String> params = new HashMap<>();
    params.put(SyntaxPlaceholders.TABLE_NAME, table.getName());
    params.put(SyntaxPlaceholders.FULL_TABLE_NAME, table.getSchema() + "." + table.getName());

    final List<String> columnDefinitions = table.getColumns().stream()
        .map(columnDefinitionBuilder::createColumnDefinitionStatement)
        .toList();

    params.put(SyntaxPlaceholders.COLUMN_DEFINITIONS, String.join(", ", columnDefinitions));

    final List<InlineConstraintDefinition> endOfBlockConstraintDefinitions = SyntaxHelper.getEndOfBlockConstraints(
        syntax);
    final List<String> endOfBlockConstraints = new ArrayList<>();

    for (InlineConstraintDefinition inlineConstraintDefinition : endOfBlockConstraintDefinitions) {
      switch (inlineConstraintDefinition.getConstraintType()) {
        case PRIMARY_KEY -> {
          if (table.getPrimaryKeyConstraint() == null) {
            break;
          }

          endOfBlockConstraints.add(
              constraintDefinitionBuilder.createConstraintDefinitionStatement(
                  table.getPrimaryKeyConstraint())
          );
        }
        case UNIQUE -> {
          final List<String> constraintDefinitions = table.getUniqueConstraints()
              .stream()
              .map(constraintDefinitionBuilder::createConstraintDefinitionStatement)
              .toList();

          if (constraintDefinitions.isEmpty()) {
            break;
          }

          endOfBlockConstraints.addAll(constraintDefinitions);
        }
        case FOREIGN_KEY -> {
          final List<String> constraintDefinitions = table.getForeignKeyConstraints().stream()
              .map(constraintDefinitionBuilder::createConstraintDefinitionStatement).toList();

          if (constraintDefinitions.isEmpty()) {
            break;
          }

          endOfBlockConstraints.addAll(constraintDefinitions);
        }
        case CHECK ->
            throw new SyntaxRenderingException("CHECK constraints are not supported yet!");
        default -> throw new SyntaxRenderingException("Unsupported end-of-block constraint type: "
            + inlineConstraintDefinition.getConstraintType());
      }
    }

    params.put(SyntaxPlaceholders.END_OF_BLOCK_CONSTRAINTS,
        String.join(", ", endOfBlockConstraints));

    if (!endOfBlockConstraints.isEmpty()) {
      params.compute(SyntaxPlaceholders.COLUMN_DEFINITIONS, (k, v) -> v + ",");
    }

    final String out = StringSubstitutor.replace(
        syntax.getTableCreation().getTemplates().getCreateTable(), params, PLACEHOLDER_BEGIN,
        PLACEHOLDER_END);

    return log.exit(out);
  }
}
