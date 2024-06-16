package de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.tablewriter.syntax.SQLiteTableDefinitionBuilder;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.sql.Statement;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteTableWriter extends AbstractJDBCTableWriter {

  private final SQLiteTableDefinitionBuilder definitionBuilder = new SQLiteTableDefinitionBuilder();

  protected SQLiteTableWriter(final ConnectionManager connectionManager) {
    super(connectionManager);
    log.entry(connectionManager);
    log.exit(this);
  }

  @Override
  public void writeTableDefinition(final Table table) throws Exception {
    log.entry(table);
    final String definition = definitionBuilder.createTableDefinitionStatement(table);

    try (final Statement stmt = this.connection.createStatement()) {
      stmt.execute(definition);
    }

    log.exit();
  }

  @Override
  public void writeTableConstraints(Table ignored) throws Exception {
    // Empty implementation as this step has to be handled in the table definition
  }
}
