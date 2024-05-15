package de.hdm_stuttgart.mi.dbad.dbwarp.connection;

import java.sql.Connection;

public interface ConnectionManager {

  Connection getSourceDatabaseConnection();

  Connection getTargetDatabaseConnection();

}
