package de.hdm_stuttgart.mi.dbad.dbwarp.migration;


import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReaderFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.XSlf4j;

@Setter
@XSlf4j
@NoArgsConstructor
@SuppressWarnings("java:S6548")
public final class MigrationManager {

  private ConnectionManager connectionManager;

  public static MigrationManager getInstance() {
    log.entry();
    return log.exit(MigrationManagerHolder.INSTANCE);
  }

  public void migrate() throws SQLException {
    log.entry();

    SchemaReaderFactory schemaReaderFactory = new SchemaReaderFactory(connectionManager);
    final SchemaReader schemaReader = schemaReaderFactory.getSchemaReader();
    List<Table> tables = schemaReader.readSchema();

    log.debug(
        "Got tables from source database: {}",
        tables.stream()
            .map(Table::toString)
            .collect(Collectors.joining(", "))
    );

    log.exit();
  }

  private static class MigrationManagerHolder {

    private static final MigrationManager INSTANCE = new MigrationManager();
  }

}
