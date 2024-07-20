package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintDefinitionStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxHelper;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
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

    final List<String> endOfLineConstraints = new ArrayList<>();

    // PRIMARY KEY CONSTRAINT
    if (SyntaxHelper.getPrimaryKeyStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE
        && column.getPrimaryKey() != null) {
      endOfLineConstraints.add(
          constraintDefinitionBuilder.createConstraintDefinitionStatement(column.getPrimaryKey())
      );
    }

    // FOREIGN KEY CONSTRAINT
    if (SyntaxHelper.getForeignKeyStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE) {
      final List<ForeignKeyConstraint> foreignKeyConstraints = column.getReferencingForeignKeys();

      foreignKeyConstraints.forEach(foreignKey ->
          endOfLineConstraints.add(
              constraintDefinitionBuilder.createConstraintDefinitionStatement(foreignKey)
          )
      );
    }

    // UNIQUE CONSTRAINT
    if (SyntaxHelper.getUniqueStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE) {
      final List<UniqueConstraint> uniqueConstraints = column.getUniqueConstraints();

      uniqueConstraints.forEach(uniqueConstraint ->
          endOfLineConstraints.add(
              constraintDefinitionBuilder.createConstraintDefinitionStatement(uniqueConstraint)
          )
      );
    }

    // NOT NULL CONSTRAINT
    if (SyntaxHelper.getNotNullStrategy(syntax) == ConstraintDefinitionStrategy.END_OF_LINE
        && Boolean.FALSE.equals(column.getNullable())) {
      endOfLineConstraints.add(syntax.getTemplates().getNotNullConstraint().getValue());
    }

    final Map<String, String> params = new HashMap<>();

    // COLUMN DEFAULT

    if (column.getDefaultValue() != null) {
      final String defaultValueDefinition = StringSubstitutor.replace(
          syntax.getTemplates().getColumnDefault(),
          Map.of(SyntaxPlaceholders.DEFAULT_VALUE, column.getDefaultValue()),
          SyntaxPlaceholders.PLACEHOLDER_BEGIN,
          SyntaxPlaceholders.PLACEHOLDER_END
      );

      params.put(SyntaxPlaceholders.COLUMN_DEFAULT, defaultValueDefinition);
    } else {
      params.put(SyntaxPlaceholders.COLUMN_DEFAULT, "");
    }

    params.put(SyntaxPlaceholders.COLUMN_NAME, column.getName());
    params.put(SyntaxPlaceholders.COLUMN_TYPE,
        column.getType().getName()); // TODO: Implement configurable type mappings!
    params.put(SyntaxPlaceholders.END_OF_LINE_CONSTRAINTS, String.join(" ", endOfLineConstraints));

    final String out = StringSubstitutor.replace(
        syntax.getTemplates().getColumnDefinition(), params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END);

    return log.exit(out);
  }
}
