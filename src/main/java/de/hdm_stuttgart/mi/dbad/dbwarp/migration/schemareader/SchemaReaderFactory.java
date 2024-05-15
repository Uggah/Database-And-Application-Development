package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@AllArgsConstructor
public final class SchemaReaderFactory {

  private ConnectionManager connectionManager;

  public SchemaReader getSchemaReader() throws SQLException {
    log.entry();
    final String databaseProductName = connectionManager.getSourceDatabaseConnection().getMetaData()
        .getDatabaseProductName();

    return log.exit(switch (databaseProductName) {
      case "SQLite" -> new SQLiteSchemaReader(connectionManager);
      case "PostgreSQL" -> new PostgreSQLSchemaReader(connectionManager);
      default -> throw new IllegalArgumentException("Database is not supported by DBWarp");
    });
  }

}
