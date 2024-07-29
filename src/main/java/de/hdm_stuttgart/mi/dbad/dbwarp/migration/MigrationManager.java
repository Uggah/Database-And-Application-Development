package de.hdm_stuttgart.mi.dbad.dbwarp.migration;

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


import de.hdm_stuttgart.mi.dbad.dbwarp.config.Configuration;
import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.datawriter.DataWriter;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.datawriter.DataWriterFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReader;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.schemareader.SchemaReaderFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.TableWriter;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.TableWriterFactory;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.TableType;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.XSlf4j;

/**
 * Is used to kick off a migration. It will handle the generation of a {@link SchemaReaderFactory}.
 */
@Setter
@XSlf4j
@NoArgsConstructor
@SuppressWarnings("java:S6548")
public final class MigrationManager {

  /**
   * The {@link ConnectionManager} that is used to get the connections to the source and target
   * databases.
   */
  private ConnectionManager connectionManager;

  /**
   * Gets the singleton instance from {@link MigrationManagerHolder}.
   * @return singleton instance.
   */
  public static MigrationManager getInstance() {
    log.entry();
    return log.exit(MigrationManagerHolder.INSTANCE);
  }

  /**
   * Starts a migration from source database to target database.
   * Equivalent to calling {@link this#migrate(String)} with null schema.
   * @throws SQLException when an SQL error occurs while reading from the source database or writing to the target database.
   */
  public void migrate() throws Exception {
    log.entry();

    final Configuration configuration = Configuration.getInstance();
    final String configuredSchema = configuration.getString("schema");
    this.migrate(configuredSchema);

    log.exit();
  }


  /**
   * Starts a migration from source database to target database.
   *
   * @param schema Schema to migrate, will migrate all schemas if unspecified.
   * @throws SQLException when an SQL error occurs while reading from the source database or writing
   *                      to the target database.
   */
  public void migrate(final String schema) throws Exception {
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

    final TableWriterFactory tableWriterFactory = new TableWriterFactory(connectionManager);
    final TableWriter tableWriter = tableWriterFactory.getTableWriter();
    final DataWriterFactory dataWriterFactory = new DataWriterFactory(connectionManager);
    final DataWriter dataWriter = dataWriterFactory.getDataWriter();

    for (final Table table : tables) {
      if (schema != null && !table.getSchema().equals(schema)) {
        log.warn("Skipping {} because it is not in the schema {}.", table.getName(), schema);
        continue;
      }

      if (table.getType() != TableType.TABLE) {
        log.warn(
            "Skipping {} because it is of type {}. Migrating objects of this type is not supported yet. It will be skipped.",
            table.getName(), table.getType().toString());
        continue;
      }

      tableWriter.writeTable(table);
      dataWriter.transferData(table);
    }

    for (final Table table : tables) {
      tableWriter.writePrimaryKey(table);
      tableWriter.writeUniqueConstraints(table);
    }

    for (final Table table : tables) {
      tableWriter.writeForeignKeys(table);
    }

    log.exit();
  }

  /**
   * Holder for the singleton instance.
   */
  private static class MigrationManagerHolder {

    private static final MigrationManager INSTANCE = new MigrationManager();
  }

}
