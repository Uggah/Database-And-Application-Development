package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

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

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

public class EndToEndProvider extends ConnectionManagerProvider implements AfterEachCallback,
    BeforeEachCallback {

  private DatabaseProvider sourceProvider;
  private DatabaseProvider targetProvider;

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    final ProvideDatabases provideDatabases = extensionContext.getTestMethod().orElseThrow()
        .getAnnotation(ProvideDatabases.class);

    this.sourceProvider = provideDatabases.sourceProvider().getConstructor().newInstance();
    this.targetProvider = provideDatabases.targetProvider().getConstructor().newInstance();

    this.sourceProvider.beforeEach(extensionContext);
    this.targetProvider.initialize(null);
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    this.sourceProvider.afterEach(extensionContext);
    this.targetProvider.afterEach(extensionContext);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return new MockConnectionManager(this.sourceProvider.getConnection(),
        this.targetProvider.getConnection());
  }
}
