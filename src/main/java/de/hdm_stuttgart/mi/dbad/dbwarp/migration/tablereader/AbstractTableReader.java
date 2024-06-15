package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
@RequiredArgsConstructor
public abstract class AbstractTableReader implements TableReader {

  private final Connection connection;

  protected AbstractTableReader(ConnectionManager connectionManager) {
    log.entry(connectionManager);
    this.connection = connectionManager.getSourceDatabaseConnection();
    log.exit();
  }

  /**
   * Retrieves all non-system tables {@link Table}, that are neither temporary nor views.
   *
   * @return an unmodifiable {@link List} of all retrieved {@link Table Tables}
   * @throws SQLException if a database access error occurs
   */
  @Override
  public List<Table> readTables() throws SQLException {
    log.entry();

    final ResultSet tables = connection.getMetaData()
        .getTables(null, null, "%", new String[]{"TABLE"});

    final List<Table> outTables = new ArrayList<>();

    while (tables.next()) {
      final Table outTable = new Table(
          tables.getString("TABLE_SCHEM"),
          tables.getString("TABLE_NAME"),
          TableType.byTableTypeString(tables.getString("TABLE_TYPE"))
      );

      outTables.add(outTable);
    }

    return log.exit(Collections.unmodifiableList(outTables));
  }

  @Override
  public void close() throws Exception {
    log.entry();
    log.exit();
  }

}
