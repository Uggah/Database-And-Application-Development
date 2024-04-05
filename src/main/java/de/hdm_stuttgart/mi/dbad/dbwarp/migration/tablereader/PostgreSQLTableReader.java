package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.Column;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class PostgreSQLTableReader extends DefaultTableReader {

  protected PostgreSQLTableReader(ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit();
  }

  @Override
  protected Column readColumn(ResultSet resultSet) throws SQLException {
    final String name = resultSet.getString("COLUMN_NAME");
    final int type = resultSet.getInt("DATA_TYPE");

    final String nullability = resultSet.getString("IS_NULLABLE");
    final int size = resultSet.getInt("COLUMN_SIZE");

    final Boolean nullable;

    switch (nullability) {
      case "NO" -> nullable = false;
      case "YES" -> nullable = true;
      case null -> nullable = null;
      default -> throw new IllegalArgumentException(
          String.format("Unknown nullability: %s", nullability));
    }

    return new Column(
        name,
        JDBCType.valueOf(type),
        nullable,
        size
    );
  }
}
