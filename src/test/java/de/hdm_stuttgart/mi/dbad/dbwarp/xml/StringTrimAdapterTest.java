package de.hdm_stuttgart.mi.dbad.dbwarp.xml;

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
