package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.TableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column.SyntaxColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.DelegatingSyntaxConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.table.SyntaxTableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.Constraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.ConstraintDefinitionStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading.SyntaxLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

@XSlf4j
public class SyntaxTableWriter implements TableWriter {

  protected final Connection connection;
  protected final Syntax syntax;
  protected final TableDefinitionBuilder definitionBuilder;
  protected final ConstraintDefinitionBuilder<Constraint> constraintDefinitionBuilder;

  protected SyntaxTableWriter(final ConnectionManager connectionManager) throws SQLException {
    log.entry(connectionManager);
    this.connection = connectionManager.getTargetDatabaseConnection();

    this.syntax = SyntaxLoader.getInstance()
        .loadSyntax(this.connection.getMetaData().getDatabaseProductName());

    this.constraintDefinitionBuilder = new DelegatingSyntaxConstraintDefinitionBuilder(syntax);

    this.definitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        new SyntaxColumnDefinitionBuilder(syntax, constraintDefinitionBuilder),
        constraintDefinitionBuilder
    );

    log.exit(this);
  }

  @Override
  public void writeTable(final Table table) throws Exception {
    log.entry(table);
    this.writeTableDefinition(table);
    this.writeTableConstraints(table);
    log.exit();
  }

  public void writeTableDefinition(final Table table) throws Exception {
    log.entry(table);

    if (syntax.getTemplates().getCreateSchema() != null && !syntax.getTemplates().getCreateSchema()
        .isBlank()) {
      try (final Statement stmt = this.connection.createStatement()) {
        final String schemaCommand = StringSubstitutor.replace(
            syntax.getTemplates().getCreateSchema(),
            Map.of(SyntaxPlaceholders.SCHEMA_NAME, table.getSchema()),
            SyntaxPlaceholders.PLACEHOLDER_BEGIN,
            SyntaxPlaceholders.PLACEHOLDER_END
        );

        stmt.execute(schemaCommand);
      }
    }

    final String definition = definitionBuilder.createTableDefinitionStatement(table);

    try (final Statement stmt = this.connection.createStatement()) {
      stmt.execute(definition);
    }

    log.exit();
  }

  public void writeTableConstraints(final Table table) throws Exception {
    log.entry(table);

    final List<String> standaloneConstraints = new ArrayList<>();

    // PRIMARY KEY CONSTRAINT
    if (syntax.getTemplates().getPrimaryKeyConstraint().getStrategy()
        == ConstraintDefinitionStrategy.STANDALONE && table.getPrimaryKeyConstraint() != null) {
      standaloneConstraints.add(
          this.constraintDefinitionBuilder.createConstraintDefinitionStatement(
              table.getPrimaryKeyConstraint()));
    }

    // UNIQUE CONSTRAINTS
    if (syntax.getTemplates().getUniqueConstraint().getStrategy()
        == ConstraintDefinitionStrategy.STANDALONE) {
      table.getUniqueConstraints().stream()
          .map(this.constraintDefinitionBuilder::createConstraintDefinitionStatement)
          .forEach(standaloneConstraints::add);
    }

    // FOREIGN KEY CONSTRAINTS
    if (syntax.getTemplates().getForeignKeyConstraint().getStrategy()
        == ConstraintDefinitionStrategy.STANDALONE) {
      table.getForeignKeyConstraints().stream()
          .map(this.constraintDefinitionBuilder::createConstraintDefinitionStatement)
          .forEach(standaloneConstraints::add);
    }

    for (String constraint : standaloneConstraints) {
      try (final Statement stmt = this.connection.createStatement()) {
        stmt.execute(constraint);
      }
    }

    log.exit();
  }
}
