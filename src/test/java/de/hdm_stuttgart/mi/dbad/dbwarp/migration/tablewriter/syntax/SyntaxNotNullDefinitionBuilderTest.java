package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.definition.NotNullDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.LoadSyntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml.SyntaxProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConfigProvider.class)
@ExtendWith(SyntaxProvider.class)
class SyntaxNotNullDefinitionBuilderTest {

  @Test
  @LoadSyntax("constraint_tests")
  void testCreateNotNullDefinitionStatement(final Syntax syntax) {
    final Table table = new Table("some_schema", "some_table", TableType.TABLE);
    final Column column = new Column(table, "some_column", null, false, 0);

    final NotNullDefinitionBuilder notNullDefinitionBuilder = new SyntaxNotNullDefinitionBuilder(
        syntax);

    final String renderedDefinition = notNullDefinitionBuilder.createNotNullDefinitionStatement(
        column);

    assertEquals(
        "SCHEMA_NAME: some_schema, TABLE_NAME: some_table, COLUMN_NAME: some_column",
        renderedDefinition
    );
  }

}
