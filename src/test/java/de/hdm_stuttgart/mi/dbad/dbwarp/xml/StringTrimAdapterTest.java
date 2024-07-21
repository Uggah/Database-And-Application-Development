package de.hdm_stuttgart.mi.dbad.dbwarp.xml;

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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringTrimAdapterTest {

  @ValueSource(strings = {" test ", "test", "", " "})
  @ParameterizedTest
  void testUnmarshal(final String param) {
    StringTrimAdapter adapter = new StringTrimAdapter();

    assertEquals(param.trim(), adapter.unmarshal(param));
  }

  @Test
  void testUnmarshal_Null() {
    StringTrimAdapter adapter = new StringTrimAdapter();

    assertNull(adapter.unmarshal(null));
  }

  @ValueSource(strings = {" test ", "test", "", " "})
  @ParameterizedTest
  void testMarshal(final String param) {
    StringTrimAdapter adapter = new StringTrimAdapter();

    assertEquals(param, adapter.marshal(param));
  }

  @Test
  void testMarshal_Null() {
    StringTrimAdapter adapter = new StringTrimAdapter();

    assertNull(adapter.marshal(null));
  }

}
