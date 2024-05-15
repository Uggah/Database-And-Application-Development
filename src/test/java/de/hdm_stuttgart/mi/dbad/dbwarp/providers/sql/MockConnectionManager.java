package de.hdm_stuttgart.mi.dbad.dbwarp.providers.sql;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import java.sql.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class MockConnectionManager implements ConnectionManager {

  private final Connection sourceDatabaseConnection;
  private final Connection targetDatabaseConnection;

}
