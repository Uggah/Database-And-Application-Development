package de.hdm_stuttgart.mi.dbad.dbwarp.model.table;

import java.sql.DatabaseMetaData;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

/**
 * Internal representation of table types as per JDBC documentation. See:
 * {@link DatabaseMetaData#getTableTypes()}
 */
@XSlf4j
@RequiredArgsConstructor
public enum TableType {

  /**
   * Default tables.
   */
  TABLE("TABLE"),

  /**
   * Views.
   */
  VIEW("VIEW"),

  /**
   * System tables. These are tables that are created and managed by the DBMS. E.g.:
   * "sqlite_schema", "pg_settings" or "information_schema"
   */
  SYSTEM_TABLE("SYSTEM TABLE"),

  /**
   * Global temporary tables. These are temporary tables managed by the DBMS which are typically
   * deleted once every session referencing them has ended. They are visible to everyone and can be
   * created manually. Only available in select DBMS.
   */
  GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),

  /**
   * Local temporary tables. These are temporary tables managed by the DBMS which are typically only
   * persisted and visible the session they were created in. Only available in select DBMS.
   */
  LOCAL_TEMPORARY("LOCAL TEMPORARY"),
  ALIAS("ALIAS"),
  SYNONYM("SYNONYM");

  private final String jdbcTableType;

  /**
   * Returns the enum constant associated with the specified JDBC table type.
   *
   * @param tableTypeString the JDBC table type to return the enum constant for
   * @return the enum constant associated with the specified JDBC table type
   */
  public static TableType byTableTypeString(String tableTypeString) {
    log.entry(tableTypeString);

    final TableType tableType = Arrays.stream(TableType.values())
        .filter(type -> type.jdbcTableType.equalsIgnoreCase(tableTypeString))
        .findAny()
        .orElseThrow(
            () -> new IllegalArgumentException(
                String.format("Unknown JDBC table type %s", tableTypeString)
            )
        );

    return log.exit(tableType);
  }
}
