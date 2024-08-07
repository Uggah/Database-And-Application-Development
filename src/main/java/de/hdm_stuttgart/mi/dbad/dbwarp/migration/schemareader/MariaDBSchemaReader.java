package de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader;

/*-
 * #%L
 * DBWarp
 * %%
 * Copyright (C) 2024 Kay Knöpfle, Lucca Greschner and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader.MariaDBColumnReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.constraintreader.MariaDBConstraintReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablereader.MariaDBTableReader;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

/**
 * Implementation of a {@link SchemaReader} for MariaDB.
 */
@XSlf4j
public class MariaDBSchemaReader extends AbstractJDBCSchemaReader {

  protected MariaDBSchemaReader(ConnectionManager connectionManager) throws SQLException {
    super(connectionManager,
        new MariaDBTableReader(connectionManager),
        new MariaDBColumnReader(connectionManager),
        new MariaDBConstraintReader(connectionManager)
    );
    log.entry(connectionManager);
    log.exit();
  }

}
