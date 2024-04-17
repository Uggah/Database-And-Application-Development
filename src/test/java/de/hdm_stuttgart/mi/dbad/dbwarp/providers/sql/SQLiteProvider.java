package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class SQLiteProvider implements ParameterResolver, AfterEachCallback {

  private Connection connection;

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    connection.close();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().equals(ConnectionManager.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    final InitializeDatabase initializeDatabase = extensionContext.getTestMethod()
        .orElseThrow()
        .getAnnotation(InitializeDatabase.class);

    if (initializeDatabase == null) {
      throw new ParameterResolutionException("SQLiteProvider requires a database path to be set!");
    }

    try {
      this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");

      ScriptUtils.executeSqlScript(
          connection,
          new EncodedResource(new ClassPathResource(initializeDatabase.value()))
      );

      return new MockConnectionManager(this.connection, null);
    } catch (SQLException e) {
      throw new ParameterResolutionException("Connection could not be established!", e);
    }
  }
}
