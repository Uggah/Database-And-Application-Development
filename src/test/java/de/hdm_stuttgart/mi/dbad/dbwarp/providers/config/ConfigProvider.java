package de.hdm_stuttgart.mi.dbad.dbwarp.providers.config;

import de.hdm_stuttgart.mi.dbad.dbwarp.config.Configuration;
import java.util.Collections;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConfigProvider implements BeforeEachCallback {

  @Override
  public void beforeEach(final ExtensionContext context) {
    Configuration.getInstance().configure(Collections.emptyMap());
  }

}
