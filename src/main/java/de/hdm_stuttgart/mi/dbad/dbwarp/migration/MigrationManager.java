package de.hdm_stuttgart.mi.dbad.dbwarp.migration;


import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.TableReaderFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Table;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.XSlf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@XSlf4j
@NoArgsConstructor
public final class MigrationManager {

  private static class MigrationManagerHolder {
    private static final MigrationManager INSTANCE = new MigrationManager();
  }

  private ConnectionManager connectionManager;

  public static MigrationManager getInstance() {
    log.entry();
    return log.exit(MigrationManagerHolder.INSTANCE);
  }

  public void migrate() throws SQLException {
    log.entry();

    TableReaderFactory.getInstance().setConnectionManager(connectionManager);
    final TableReader tableReader = TableReaderFactory.getInstance().getTableReader();
    List<Table> tables = tableReader.readTables();

    log.debug(
            "Got tables from source database: {}",
            tables.stream()
                    .map(Table::toString)
                    .collect(Collectors.joining(", "))
    );

    log.exit();
  }

}
