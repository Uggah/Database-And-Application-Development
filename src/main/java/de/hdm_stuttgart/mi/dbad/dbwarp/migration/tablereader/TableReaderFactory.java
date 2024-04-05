package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.SQLException;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableReaderFactory {

  private static TableReaderFactory INSTANCE;

  @Setter
  private ConnectionManager connectionManager;

  public static TableReaderFactory getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TableReaderFactory();
    }

    return INSTANCE;
  }

  public TableReader getTableReader() throws SQLException {

    switch (connectionManager.getSourceDatabaseConnection().getMetaData()
        .getDatabaseProductName()) {
      case "SQLite":
        return new SQLiteTableReader(connectionManager);
      default:
        throw new IllegalArgumentException("Database is not supported by DBWarp");
    }

  }

}
