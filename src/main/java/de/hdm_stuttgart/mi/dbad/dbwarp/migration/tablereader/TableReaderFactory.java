package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.SQLException;
import lombok.*;

@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableReaderFactory {

  private static class TableReaderFactoryHolder {
    private static final TableReaderFactory INSTANCE = new TableReaderFactory();
  }

  private ConnectionManager connectionManager;

  public static TableReaderFactory getInstance() {
    return TableReaderFactoryHolder.INSTANCE;
  }

  public TableReader getTableReader() throws SQLException {
    final String databaseProductName = connectionManager.getSourceDatabaseConnection().getMetaData().getDatabaseProductName();

    return switch (databaseProductName) {
      case "SQLite" -> new SQLiteTableReader(connectionManager);
      default -> throw new IllegalArgumentException("Database is not supported by DBWarp");
    };
  }

}
