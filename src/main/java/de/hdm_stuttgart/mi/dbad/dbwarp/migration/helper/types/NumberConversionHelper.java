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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NumberConversionHelper extends TypeConversionHelper {

  private final Number number;

  @Override
  public String toString() {
    return number.toString();
  }

  @Override
  public Integer toInteger() {
    return number.intValue();
  }

  @Override
  public Double toDouble() {
    return number.doubleValue();
  }

  @Override
  public Float toFloat() {
    return number.floatValue();
  }

  @Override
  public Boolean toBoolean() {
    return Double.compare(number.doubleValue(), 0d) > 0;
  }

}
