package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.GenerationStrategyDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.GenerationStrategy;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(ConfigProvider.class)
@ExtendWith(SyntaxProvider.class)
class SyntaxGenerationStrategyDefinitionBuilderTest {

  @ParameterizedTest
  @LoadSyntax("constraint_tests")
  @ValueSource(strings = {"SERIAL", "IDENTITY"})
  void testCreateGenerationStrategyDefinitionStatement(final String generationStrategy,
      final Syntax syntax) {
    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", null, false, 0);
    column.setGenerationStrategy(GenerationStrategy.valueOf(generationStrategy));

    final GenerationStrategyDefinitionBuilder generationStrategyDefinitionBuilder = new SyntaxGenerationStrategyDefinitionBuilder(
        syntax);

    final String renderedDefinition = generationStrategyDefinitionBuilder.createGenerationStrategyDefinitionStatement(
        column);

    assertEquals(
        "SCHEMA_NAME: some_schema, TABLE_NAME: some_table, COLUMN_NAME: some_column",
        renderedDefinition
    );
  }
}
