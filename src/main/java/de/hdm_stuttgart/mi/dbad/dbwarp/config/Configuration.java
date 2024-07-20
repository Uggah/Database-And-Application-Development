package de.hdm_stuttgart.mi.dbad.dbwarp.config;

import java.util.Map;

/**
 * Singleton class for configuration.
 */
public class Configuration {

  private static Configuration instance;

  private Configuration() {
  }

  /**
   * Get the singleton instance of the configuration.
   *
   * @return The configuration instance.
   */
  public static Configuration getInstance() {
    if (instance == null) {
      instance = new Configuration();
    }

    return instance;
  }

  /**
   * The configuration map contains all configuration properties.
   *
   * @see Configuration#configure(Map)
   */
  private Map<String, Object> configurationMap;

  private boolean configured = false;

  /**
   * Configure the configuration with a map of properties.
   *
   * @param configuration The configuration properties.
   */
  public void configure(final Map<String, Object> configuration) {
    this.configured = true;
    this.configurationMap = configuration;
  }

  // STRING

  /**
   * Get a string property from the configuration.
   *
   * @param property The property name.
   * @return The property value.
   */
  public String getString(final String property) {
    return (String) getValue(property, String.class);
  }

  // NUMBER TYPES

  /**
   * Get an integer property from the configuration.
   *
   * @param property The property name.
   * @return The property value.
   */
  public int getInt(final String property) {
    return (Integer) getValue(property, Integer.class);
  }

  /**
   * Get a long property from the configuration.
   *
   * @param property The property name.
   * @return The property value.
   */
  public long getLong(final String property) {
    return (Long) getValue(property, Long.class);
  }

  /**
   * Get a double property from the configuration.
   *
   * @param property The property name.
   * @return The property value.
   */
  public double getDouble(final String property) {
    return (Double) getValue(property, Double.class);
  }

  // BOOLEAN

  /**
   * Get a boolean property from the configuration.
   *
   * @param property The property name.
   * @return The property value.
   */
  public boolean getBoolean(final String property) {
    return (Boolean) getValue(property, Boolean.class);
  }

  /**
   * Get a property from the configuration. Will throw an exception if the property is not found or
   * not of the correct type.
   *
   * @param property    The property name.
   * @param targetClass The target class the property will ultimately be cast to.
   * @return The property value.
   */
  private Object getValue(final String property, final Class<?> targetClass) {
    if (!configured) {
      throw new IllegalStateException("Configuration not set");
    }

    final Object value = configurationMap.get(property);

    if (value != null && !targetClass.isInstance(value)) {
      throw new IllegalArgumentException("Property is not of type " + targetClass.getName());
    }

    return value;
  }

}
