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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.ConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.GenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.NotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.TableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.SyntaxGenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.SyntaxNotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column.SyntaxColumnDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column.type.ColumnTypeMapper;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column.type.SyntaxColumnTypeMapper;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.SyntaxForeignKeyConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.SyntaxPrimaryKeyConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.constraints.SyntaxUniqueConstraintDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.table.SyntaxTableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.ForeignKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.PrimaryKeyConstraint;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.constraints.UniqueConstraint;
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
import java.util.Objects;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.text.StringSubstitutor;

@XSlf4j
public class SyntaxTableWriter implements TableWriter {

  private final Connection connection;

  private final Syntax syntax;
  private final TableDefinitionBuilder definitionBuilder;
  private final ConstraintDefinitionBuilder<PrimaryKeyConstraint> primaryKeyConstraintDefinitionBuilder;
  private final ConstraintDefinitionBuilder<ForeignKeyConstraint> foreignKeyConstraintDefinitionBuilder;
  private final ConstraintDefinitionBuilder<UniqueConstraint> uniqueConstraintDefinitionBuilder;
  private final NotNullDefinitionBuilder notNullDefinitionBuilder;
  private final GenerationStrategyDefinitionBuilder generationStrategyDefinitionBuilder;
  private final ColumnTypeMapper columnTypeMapper;

  protected SyntaxTableWriter(final ConnectionManager connectionManager) throws SQLException {
    log.entry(connectionManager);
    this.connection = connectionManager.getTargetDatabaseConnection();

    this.syntax = new SyntaxLoader().loadSyntax(
        this.connection.getMetaData().getDatabaseProductName());

    this.primaryKeyConstraintDefinitionBuilder = new SyntaxPrimaryKeyConstraintDefinitionBuilder(
        syntax);
    this.foreignKeyConstraintDefinitionBuilder = new SyntaxForeignKeyConstraintDefinitionBuilder(
        syntax);
    this.uniqueConstraintDefinitionBuilder = new SyntaxUniqueConstraintDefinitionBuilder(syntax);
    this.notNullDefinitionBuilder = new SyntaxNotNullDefinitionBuilder(syntax);
    this.generationStrategyDefinitionBuilder = new SyntaxGenerationStrategyDefinitionBuilder(
        syntax);
    this.columnTypeMapper = new SyntaxColumnTypeMapper(syntax);

    final ColumnDefinitionBuilder columnDefinitionBuilder = new SyntaxColumnDefinitionBuilder(
        syntax,
        this.primaryKeyConstraintDefinitionBuilder,
        this.foreignKeyConstraintDefinitionBuilder,
        this.uniqueConstraintDefinitionBuilder,
        this.notNullDefinitionBuilder,
        this.generationStrategyDefinitionBuilder,
        this.columnTypeMapper
    );

    this.definitionBuilder = new SyntaxTableDefinitionBuilder(
        syntax,
        columnDefinitionBuilder,
        this.primaryKeyConstraintDefinitionBuilder,
        this.foreignKeyConstraintDefinitionBuilder,
        this.uniqueConstraintDefinitionBuilder,
        this.notNullDefinitionBuilder,
        this.generationStrategyDefinitionBuilder
    );

    log.exit(this);
  }

  @Override
  public void writeTable(final Table table) throws Exception {
    log.entry(table);

    if (syntax.getTemplates().getCreateSchema() != null && !syntax.getTemplates().getCreateSchema()
        .isBlank() && table.getSchema() != null) {
      try (final Statement stmt = this.connection.createStatement()) {
        final String schemaCommand = StringSubstitutor.replace(
            syntax.getTemplates().getCreateSchema(),
            Map.of(SyntaxPlaceholders.SCHEMA_NAME,
                Objects.requireNonNullElse(table.getSchema(), syntax.getDefaultSchema())),
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

  @Override
  public void writePrimaryKey(Table table) throws Exception {
    log.entry(table);
    final List<String> standaloneConstraints = new ArrayList<>();

    if (syntax.getTemplates().getPrimaryKeyConstraint().getStrategy()
        == ConstraintDefinitionStrategy.STANDALONE && table.getPrimaryKeyConstraint() != null) {
      standaloneConstraints.add(
          this.primaryKeyConstraintDefinitionBuilder.createConstraintDefinitionStatement(
              table.getPrimaryKeyConstraint()));
    }

    try (final Statement stmt = this.connection.createStatement()) {
      for (String constraint : standaloneConstraints) {
        // The statements cannot be executed in a batch as adding multiple, comma-separated
        // SQL statements using a single addBatch() call is not supported in some JDBC drivers.
        // See: https://www.postgresql.org/message-id/373352.78463.qm@web32701.mail.mud.yahoo.com
        stmt.execute(constraint);
      }
    }
    log.exit();
  }

  @Override
  public void writeUniqueConstraints(Table table) throws Exception {
    log.entry(table);
    final List<String> standaloneConstraints = new ArrayList<>();

    if (syntax.getTemplates().getUniqueConstraint().getStrategy()
        == ConstraintDefinitionStrategy.STANDALONE) {
      table.getUniqueConstraints().stream()
          .map(this.uniqueConstraintDefinitionBuilder::createConstraintDefinitionStatement)
          .forEach(standaloneConstraints::add);
    }

    try (final Statement stmt = this.connection.createStatement()) {
      for (String constraint : standaloneConstraints) {
        // The statements cannot be executed in a batch as adding multiple, comma-separated
        // SQL statements using a single addBatch() call is not supported in some JDBC drivers.
        // See: https://www.postgresql.org/message-id/373352.78463.qm@web32701.mail.mud.yahoo.com
        stmt.execute(constraint);
      }
    }
    log.exit();
  }

  @Override
  public void writeForeignKeys(Table table) throws Exception {
    log.entry(table);
    final List<String> standaloneConstraints = new ArrayList<>();

    if (syntax.getTemplates().getForeignKeyConstraint().getStrategy()
        == ConstraintDefinitionStrategy.STANDALONE) {
      table.getForeignKeyConstraints().stream()
          .map(this.foreignKeyConstraintDefinitionBuilder::createConstraintDefinitionStatement)
          .forEach(standaloneConstraints::add);
    }

    try (final Statement stmt = this.connection.createStatement()) {
      for (String constraint : standaloneConstraints) {
        // The statements cannot be executed in a batch as adding multiple, comma-separated
        // SQL statements using a single addBatch() call is not supported in some JDBC drivers.
        // See: https://www.postgresql.org/message-id/373352.78463.qm@web32701.mail.mud.yahoo.com
        stmt.execute(constraint);
      }
    }
    log.exit();
  }

}
