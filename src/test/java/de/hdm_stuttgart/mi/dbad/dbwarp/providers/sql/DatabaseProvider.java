package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

public abstract class DatabaseProvider extends ConnectionManagerProvider implements
    BeforeEachCallback, AfterEachCallback {

  public abstract void initialize(InitializeDatabase initializeDatabase) throws SQLException;

  public abstract Connection getConnection();

  @Override
  public final void beforeEach(ExtensionContext extensionContext) throws Exception {
    final Method testMethod = extensionContext.getTestMethod().orElseThrow();
    final InitializeDatabase initializeDatabase = testMethod.getAnnotation(
        InitializeDatabase.class);

    this.initialize(initializeDatabase);
  }

  @Override
  public final Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return new MockConnectionManager(this.getConnection(), null);
  }

}
