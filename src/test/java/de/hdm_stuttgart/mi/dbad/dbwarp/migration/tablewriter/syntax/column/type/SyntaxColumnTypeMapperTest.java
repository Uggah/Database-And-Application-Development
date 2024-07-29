package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.column.type;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.config.Configuration;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading.SyntaxLoader;
import java.sql.JDBCType;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SyntaxColumnTypeMapperTest {

  Syntax syntax;

  @BeforeEach
  void setUp() {
    Configuration.getInstance().configure(Collections.emptyMap());
    this.syntax = new SyntaxLoader().loadSyntax("MariaDB");
  }

  @Test
  void testMapperWithoutMapping() {
    // Given
    final SyntaxColumnTypeMapper mapper = new SyntaxColumnTypeMapper(syntax);
    Column column = new Column(null, "NCLOB", JDBCType.NCLOB, false, 500);

    // When
    final String result = mapper.map(column);

    // Then
    assertEquals("NCLOB", result);
  }

  @Test
  void testMapperWithSingleMapping() {
    // Given
    final SyntaxColumnTypeMapper mapper = new SyntaxColumnTypeMapper(syntax);
    Column column = new Column(null, "LONGVARCHAR", JDBCType.LONGVARCHAR, false, 500);

    // When
    final String result = mapper.map(column);

    // Then
    assertEquals("MEDIUMTEXT", result);
  }

  @Test
  void testMapperWithMappingAndSize() {
    // Given
    final SyntaxColumnTypeMapper mapper = new SyntaxColumnTypeMapper(syntax);
    Column column = new Column(null, "VARCHAR", JDBCType.VARCHAR, false, 500);
    Column column2 = new Column(null, "VARCHAR", JDBCType.VARCHAR, false, 1000);

    // When
    final String result = mapper.map(column);
    final String result2 = mapper.map(column2);

    // Then
    assertEquals("VARCHAR(500)", result);
    assertEquals("VARCHAR(1000)", result2);
  }

}
