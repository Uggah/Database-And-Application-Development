package de.hdm_stuttgart.mi.dbad.dbwarp.migration;


import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.databasemodel.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReaderFactory;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

@NoArgsConstructor
public final class MigrationManager {

  private static MigrationManager INSTANCE;

  @Setter
  private ConnectionManager connectionManager;

  public static MigrationManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MigrationManager();
    }

    return INSTANCE;
  }

  @SneakyThrows
  public void migrate() {
    TableReaderFactory.getInstance().setConnectionManager(connectionManager);
    TableReader tableReader = TableReaderFactory.getInstance().getTableReader();
    Table[] tables = tableReader.readTables();
  }

}
