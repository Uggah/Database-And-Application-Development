package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@RequiredArgsConstructor
public class TableWriterFactory {

  private final ConnectionManager connectionManager;

  public TableWriter getTableWriter() throws SQLException {
    log.entry();
    final String databaseProductName = connectionManager.getTargetDatabaseConnection().getMetaData()
        .getDatabaseProductName();

    return log.exit(switch (databaseProductName) {
      case "SQLite" -> new SQLiteTableWriter(connectionManager);
      default -> throw new IllegalArgumentException("Database is not supported by DBWarp");
    });
  }

}
