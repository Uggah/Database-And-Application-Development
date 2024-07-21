package de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading;

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

import static org.junit.jupiter.api.Assertions.*;

import de.hdm_stuttgart.mi.dbad.dbwarp.config.Configuration;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.exception.SyntaxLoadException;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.providers.config.ConfigProvider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConfigProvider.class)
class SyntaxLoaderTest {

  @Test
  void testLoadSyntaxFromResources() {
    final SyntaxLoader syntaxLoader = new SyntaxLoader();
    final Syntax syntax = syntaxLoader.loadSyntax("example_syntax");

    assertNotNull(syntax);
    assertEquals("EXAMPLE_CREATE_SCHEMA", syntax.getTemplates().getCreateSchema());
    assertEquals("EXAMPLE_CREATE_TABLE", syntax.getTemplates().getCreateTable());
    assertEquals("EXAMPLE_COLUMN_DEFINITION", syntax.getTemplates().getColumnDefinition());
    assertEquals("EXAMPLE_NOT_NULL_CONSTRAINT",
        syntax.getTemplates().getNotNullConstraint().getValue());
    assertEquals("EXAMPLE_PRIMARY_KEY_CONSTRAINT",
        syntax.getTemplates().getPrimaryKeyConstraint().getValue());
    assertEquals("EXAMPLE_FOREIGN_KEY_CONSTRAINT",
        syntax.getTemplates().getForeignKeyConstraint().getValue());
    assertEquals("EXAMPLE_UNIQUE_CONSTRAINT",
        syntax.getTemplates().getUniqueConstraint().getValue());
    assertEquals("EXAMPLE_COLUMN_DEFAULT", syntax.getTemplates().getColumnDefault());
  }

  @Test
  void testLoadSyntaxFromResources_UppercaseName() {
    final SyntaxLoader syntaxLoader = new SyntaxLoader();
    final Syntax syntax = syntaxLoader.loadSyntax("EXAMPLE_SYNTAX");

    assertNotNull(syntax);
    assertEquals("EXAMPLE_CREATE_SCHEMA", syntax.getTemplates().getCreateSchema());
    assertEquals("EXAMPLE_CREATE_TABLE", syntax.getTemplates().getCreateTable());
    assertEquals("EXAMPLE_COLUMN_DEFINITION", syntax.getTemplates().getColumnDefinition());
    assertEquals("EXAMPLE_NOT_NULL_CONSTRAINT",
        syntax.getTemplates().getNotNullConstraint().getValue());
    assertEquals("EXAMPLE_PRIMARY_KEY_CONSTRAINT",
        syntax.getTemplates().getPrimaryKeyConstraint().getValue());
    assertEquals("EXAMPLE_FOREIGN_KEY_CONSTRAINT",
        syntax.getTemplates().getForeignKeyConstraint().getValue());
    assertEquals("EXAMPLE_UNIQUE_CONSTRAINT",
        syntax.getTemplates().getUniqueConstraint().getValue());
    assertEquals("EXAMPLE_COLUMN_DEFAULT", syntax.getTemplates().getColumnDefault());
  }

  @Test
  void testLoadSyntaxFromResource_InvalidSyntax() {
    final SyntaxLoader syntaxLoader = new SyntaxLoader();

    assertThrows(SyntaxLoadException.class, () -> syntaxLoader.loadSyntax("invalid_syntax"));
  }

  @Test
  void testLoadSyntaxFromFile() throws IOException {
    final Path path = Path.of("./example_syntax.xml");
    try (final InputStream inputStream = getClass().getResourceAsStream(
        "/syntaxes/example_syntax.xml")) {
      assertNotNull(inputStream);
      Files.copy(inputStream, path);
    }

    final Configuration configuration = Configuration.getInstance();
    configuration.configure(
        Map.of("syntax", Map.of("some_random_database", "./example_syntax.xml")));

    final SyntaxLoader syntaxLoader = new SyntaxLoader();

    final Syntax syntax = syntaxLoader.loadSyntax("some_random_database");

    assertNotNull(syntax);
    assertEquals("EXAMPLE_CREATE_SCHEMA", syntax.getTemplates().getCreateSchema());
    assertEquals("EXAMPLE_CREATE_TABLE", syntax.getTemplates().getCreateTable());
    assertEquals("EXAMPLE_COLUMN_DEFINITION", syntax.getTemplates().getColumnDefinition());
    assertEquals("EXAMPLE_NOT_NULL_CONSTRAINT",
        syntax.getTemplates().getNotNullConstraint().getValue());
    assertEquals("EXAMPLE_PRIMARY_KEY_CONSTRAINT",
        syntax.getTemplates().getPrimaryKeyConstraint().getValue());
    assertEquals("EXAMPLE_FOREIGN_KEY_CONSTRAINT",
        syntax.getTemplates().getForeignKeyConstraint().getValue());
    assertEquals("EXAMPLE_UNIQUE_CONSTRAINT",
        syntax.getTemplates().getUniqueConstraint().getValue());
    assertEquals("EXAMPLE_COLUMN_DEFAULT", syntax.getTemplates().getColumnDefault());

    Files.delete(path);
  }

  @Test
  void testLoadSyntaxFromFile_InvalidSyntax() throws IOException {
    final Path path = Path.of("./invalid_syntax.xml");
    try (final InputStream inputStream = getClass().getResourceAsStream(
        "/syntaxes/invalid_syntax.xml")) {
      assertNotNull(inputStream);
      Files.copy(inputStream, path);
    }

    final Configuration configuration = Configuration.getInstance();
    configuration.configure(
        Map.of("syntax", Map.of("some_random_database", "./invalid_syntax.xml")));

    final SyntaxLoader syntaxLoader = new SyntaxLoader();

    assertThrows(SyntaxLoadException.class, () -> syntaxLoader.loadSyntax("some_random_database"));

    Files.delete(path);
  }


}
