package de.hdm_stuttgart.mi.dbad.dbwarp.migration.datawriter;

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
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.datawriter.exceptions.DataWritingException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.SyntaxPlaceholders;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading.SyntaxLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

@RequiredArgsConstructor
public class SyntaxDataWriter implements DataWriter {

  private final Connection sourceConnection;
  private final Connection targetConnection;

  private final Syntax sourceSyntax;
  private final Syntax targetSyntax;

  public SyntaxDataWriter(final ConnectionManager connectionManager) throws SQLException {
    this.sourceConnection = connectionManager.getSourceDatabaseConnection();
    this.targetConnection = connectionManager.getTargetDatabaseConnection();

    this.sourceSyntax = new SyntaxLoader().loadSyntax(
        this.sourceConnection.getMetaData().getDatabaseProductName());

    this.targetSyntax = new SyntaxLoader().loadSyntax(
        this.targetConnection.getMetaData().getDatabaseProductName());
  }

  @Override
  public void transferData(final Table table) {
    final List<Column> columnList = table.getColumns();

    try (final PreparedStatement selectStatement = prepareSelectStatement(
        table); final PreparedStatement insertStatement = prepareInsertStatement(table)) {
      final ResultSet resultSet = selectStatement.executeQuery();

      while (resultSet.next()) {
        for (int i = 0; i < columnList.size(); i++) {
          final Object value = resultSet.getObject(i + 1);

          insertStatement.setObject(i + 1, value);
        }

        insertStatement.addBatch();
      }

      insertStatement.executeBatch();
    } catch (SQLException e) {
      throw new DataWritingException("Failed to transfer data", e);
    }
  }

  private PreparedStatement prepareSelectStatement(final Table table) {
    final String selectStatementTemplate = sourceSyntax.getTemplates().getSelectData();

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.SCHEMA_NAME,
        Objects.requireNonNullElse(table.getSchema(), sourceSyntax.getDefaultSchema()));
    params.put(SyntaxPlaceholders.TABLE_NAME, table.getName());

    params.put(
        SyntaxPlaceholders.COLUMN_NAMES,
        String.join(", ", table.getColumns().stream()
            .map(Column::getName)
            .toList()
        )
    );

    final String rawStatement = StringSubstitutor.replace(
        selectStatementTemplate,
        params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END
    );

    try {
      return sourceConnection.prepareStatement(rawStatement);
    } catch (SQLException e) {
      throw new DataWritingException("Failed to prepare select statement", e);
    }
  }

  private PreparedStatement prepareInsertStatement(final Table table) {
    final String insertStatementTemplate = targetSyntax.getTemplates().getInsertData();

    final Map<String, String> params = new HashMap<>();

    params.put(SyntaxPlaceholders.SCHEMA_NAME,
        Objects.requireNonNullElse(table.getSchema(), targetSyntax.getDefaultSchema()));
    params.put(SyntaxPlaceholders.TABLE_NAME, table.getName());

    params.put(
        SyntaxPlaceholders.COLUMN_NAMES,
        String.join(", ", table.getColumns().stream()
            .map(Column::getName)
            .toArray(String[]::new)
        )
    );

    params.put(
        SyntaxPlaceholders.COLUMN_VALUES,
        String.join(", ", table.getColumns().stream()
            .map(c -> "?")
            .toArray(String[]::new)
        )
    );

    final String rawStatement = StringSubstitutor.replace(
        insertStatementTemplate,
        params,
        SyntaxPlaceholders.PLACEHOLDER_BEGIN,
        SyntaxPlaceholders.PLACEHOLDER_END
    );

    try {
      return targetConnection.prepareStatement(rawStatement);
    } catch (SQLException e) {
      throw new DataWritingException("Failed to prepare insert statement", e);
    }
  }
}
