package de.hdm_stuttgart.mi.dbad.dbwarp.providers.xml;

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
    final SyntaxLoader syntaxLoader = SyntaxLoader.getInstance();

    final Method testMethod = extensionContext.getTestMethod().orElseThrow();
    final LoadSyntax loadSyntax = testMethod.getAnnotation(LoadSyntax.class);

    return syntaxLoader.loadSyntax(loadSyntax.value());
  }
}
