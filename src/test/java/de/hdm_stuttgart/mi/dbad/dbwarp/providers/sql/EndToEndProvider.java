package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

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
