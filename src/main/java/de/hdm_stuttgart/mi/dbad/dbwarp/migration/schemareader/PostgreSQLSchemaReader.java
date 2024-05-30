package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader.PostgreSQLColumnReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader.PostgreSQLConstraintReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.PostgreSQLTableReader;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class PostgreSQLSchemaReader extends AbstractJDBCSchemaReader {

  protected PostgreSQLSchemaReader(ConnectionManager connectionManager) throws SQLException {
    super(connectionManager,
        new PostgreSQLTableReader(connectionManager),
        new PostgreSQLColumnReader(connectionManager),
        new PostgreSQLConstraintReader(connectionManager)
    );
    log.entry(connectionManager);
    log.exit();
  }

}
