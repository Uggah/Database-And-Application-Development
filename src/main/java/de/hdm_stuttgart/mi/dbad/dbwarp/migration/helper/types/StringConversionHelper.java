package de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types;

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

class StringConversionHelper extends TypeConversionHelper {

  private final String string;

  StringConversionHelper(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  public Integer toInteger() {
    return Integer.parseInt(string);
  }

  @Override
  public Double toDouble() {
    return Double.parseDouble(string);
  }

  @Override
  public Float toFloat() {
    return Float.parseFloat(string);
  }

  @Override
  public Boolean toBoolean() {
    return string != null;
  }

}
