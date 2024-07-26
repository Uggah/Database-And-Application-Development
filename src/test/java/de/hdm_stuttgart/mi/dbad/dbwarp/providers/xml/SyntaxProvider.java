package de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml;

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

import de.hdm_stuttgart.mi.dbad.dbwarp.syntax.loading.SyntaxLoader;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.syntax.Syntax;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * This class is used to provide a Syntax object for testing purposes.
 */
public class SyntaxProvider implements ParameterResolver {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().equals(Syntax.class);
  }

  /**
   * Resolves the Syntax object for testing purposes.
   *
   * @return Syntax object
   */
  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    final SyntaxLoader syntaxLoader = new SyntaxLoader();

    final Method testMethod = extensionContext.getTestMethod().orElseThrow();
    final LoadSyntax loadSyntax = testMethod.getAnnotation(LoadSyntax.class);

    return syntaxLoader.loadSyntax(loadSyntax.value());
  }
}
