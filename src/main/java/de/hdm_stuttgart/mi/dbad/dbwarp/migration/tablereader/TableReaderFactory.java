package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.SQLException;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableReaderFactory {

  private static class TableReaderFactoryHolder {
    private static final TableReaderFactory INSTANCE = new TableReaderFactory();
  }

  private ConnectionManager connectionManager;

  public static TableReaderFactory getInstance() {
    log.entry();
    return log.exit(TableReaderFactoryHolder.INSTANCE);
  }

  public TableReader getTableReader() throws SQLException {
    log.entry();
    final String databaseProductName = connectionManager.getSourceDatabaseConnection().getMetaData().getDatabaseProductName();

    return log.exit(switch (databaseProductName) {
      case "SQLite" -> new SQLiteTableReader(connectionManager);
      default -> throw new IllegalArgumentException("Database is not supported by DBWarp");
    });
  }

}
