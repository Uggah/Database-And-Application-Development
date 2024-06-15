package de.hdm_stuttgart.mi.dbad.dbwarp.migration.columnreader;

import de.hdm_stuttgart.mi.dbad.dbwarp.connection.ConnectionManager;
import de.hdm_stuttgart.mi.dbad.dbwarp.migration.helper.types.TypeConversionHelper;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.column.Column;
import de.hdm_stuttgart.mi.dbad.dbwarp.model.table.Table;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class SQLiteColumnReader extends AbstractColumnReader {

  private final PreparedStatement columnDefaultPreparedStatement;

  public SQLiteColumnReader(
      ConnectionManager connectionManager) throws SQLException {
    super(connectionManager);
    log.entry(connectionManager);
    this.columnDefaultPreparedStatement = this.connection.prepareStatement(
        """
              SELECT dflt_value AS default_value FROM pragma_table_info(?) WHERE name = ?;
            """);
    log.exit();
  }

  @Override
  protected Column readColumn(Table table, ResultSet resultSet) throws SQLException {
    log.entry(table, resultSet);
    final Column column = super.readColumn(table, resultSet);

    this.columnDefaultPreparedStatement.setString(1, table.getName());
    this.columnDefaultPreparedStatement.setString(2, column.getName());

    final ResultSet defaultValueResultSet = this.columnDefaultPreparedStatement.executeQuery();
    defaultValueResultSet.next();

    final Object defaultValue = defaultValueResultSet.getObject("default_value");

    if (defaultValue != null) {
      column.setDefaultValue(
          TypeConversionHelper.fromString(
              (String) defaultValue
          ).toType(column.getType())
      );

      if (column.getDefaultValue() instanceof String defaultValueString) {
        // The SQLite JDBC driver somehow returns strings with enclosing apostrophes. They are stripped in the next line.
        defaultValueString = defaultValueString.substring(1, defaultValueString.length() - 1);

        column.setDefaultValue(
            defaultValueString
        );
      }
    }

    return log.exit(column);
  }
}
