package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLProvider implements BeforeEachCallback, AfterEachCallback,
    ParameterResolver {

  private final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
      "postgres:15-alpine");
  private Connection connection;

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    final Method testMethod = extensionContext.getTestMethod().orElseThrow();
    final InitializeDatabase initializeDatabase = testMethod.getAnnotation(
        InitializeDatabase.class);

    if (initializeDatabase != null && !initializeDatabase.value().isEmpty()) {
      postgreSQLContainer.withInitScript(initializeDatabase.value());
    }

    postgreSQLContainer.start();
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    this.connection.close();
    postgreSQLContainer.stop();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().equals(ConnectionManager.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    try {
      this.connection = DriverManager.getConnection(
          postgreSQLContainer.getJdbcUrl(),
          postgreSQLContainer.getUsername(),
          postgreSQLContainer.getPassword()
      );

      return new MockConnectionManager(this.connection, null);
    } catch (SQLException e) {
      throw new ParameterResolutionException("Connection could not be established!", e);
    }
  }
}
