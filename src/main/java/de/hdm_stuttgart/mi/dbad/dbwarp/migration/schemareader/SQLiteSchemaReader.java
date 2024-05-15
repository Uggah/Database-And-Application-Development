package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader.SQLiteColumnReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader.SQLiteConstraintReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.SQLiteTableReader;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteSchemaReader extends AbstractJDBCSchemaReader {

  public SQLiteSchemaReader(ConnectionManager connectionManager) throws SQLException {
    super(
        connectionManager,
        new SQLiteTableReader(connectionManager),
        new SQLiteColumnReader(connectionManager),
        new SQLiteConstraintReader(connectionManager)
    );
    log.entry(connectionManager);
    log.exit();
  }

}
