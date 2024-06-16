package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public abstract class ConnectionManagerProvider implements ParameterResolver {

  @Override
  public final boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().equals(ConnectionManager.class);
  }

}
