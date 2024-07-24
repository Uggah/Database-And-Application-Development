package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

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
