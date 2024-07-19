package de.hdm_stuttgart.mi.dbad.dbwarp.xml;

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
