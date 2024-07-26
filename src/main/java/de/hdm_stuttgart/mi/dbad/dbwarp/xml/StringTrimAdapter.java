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

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter class for trimming strings during XML unmarshalling. This class extends
 * {@link XmlAdapter} to provide custom unmarshalling logic, specifically to trim leading and
 * trailing whitespace from string values in XML. Inspired by: <a
 * href="https://stackoverflow.com/a/7420041">Sahil Muthoo</a>
 */
public class StringTrimAdapter extends XmlAdapter<String, String> {

  /**
   * Unmarshals a string by trimming leading and trailing whitespace.
   *
   * @param v The string to unmarshal and trim. May be {@code null}.
   * @return The trimmed string, or {@code null} if the input was {@code null}.
   */
  @Override
  public String unmarshal(String v) {
    if (v == null) {
      return null;
    }

    return v.trim();
  }

  /**
   * Marshals a string without modification. This method is a no-op for marshalling, returning the
   * input string as is.
   *
   * @param v The string to marshal.
   * @return The original string without any modifications.
   */
  @Override
  public String marshal(String v) {
    return v;
  }
}
