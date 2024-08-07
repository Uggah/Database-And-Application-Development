# Target Database Syntax

The syntax for DDL (Data Definition Language) statements is configured using a Syntax-File.
The syntax file is an XML file with the schema found
in [./src/main/resources/syntaxes/schema.xsd](./src/main/resources/syntaxes/schema.xsd).

An exemplary syntax file is found
in [./src/main/resources/syntaxes/postgresql.xml](./src/main/resources/syntaxes/postgresql.xml).

The default syntaxes aim to be as compatible as possible.
This will lead to datatype conversions that are very inefficient or even truncate your data (if you
save **very** long VARCHARs for example).
It is recommended to create your own syntax file for your specific use case.

This could be done by trying to migrate and then fixing and improving the syntax file
until the migration is successful and to your likings.

## Defining your own syntax

If you want to customize an existing syntax or create a new one,
you can do so by creating a new XML file that follows the schema.

The statement templates in the syntax file can contain placeholders that are replaced by the actual
values of the statement.
The placeholders are enclosed in curly braces `${}`. They differ between the use cases.
The following tables will give you an overview of the placeholders:

### Placeholders for `createSchema`

| Placeholder      | Description            |
|------------------|------------------------|
| `${schema_name}` | The name of the schema |

### Placeholders for `createTable`

| Placeholder                   | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `${schema_name}`              | The name of the schema the table is in                               |
| `${table_name}`               | The name of the table                                                |
| `${column_definitions}`       | The column definitions of the table as defined in `columnDefinition` |
| `${end_of_block_constraints}` | All constraints defined with strategy `END_OF_BLOCK`                 |

### Placeholders for `columnDefinition`

| Placeholder                  | Description                                                        |
|------------------------------|--------------------------------------------------------------------|
| `${column_name}`             | The name of the column                                             |
| `${column_type}`             | The data type of the column                                        |
| `${end_of_line_constraints}` | All constraints defined for the column with strategy `END_OF_LINE` |

### Placeholders for constraints

| Placeholder          | Description                                               |
|----------------------|-----------------------------------------------------------|
| `${constraint_name}` | The name of the constraint                                |
| `${schema_name}`     | The name of the schema the affected table is defined in   |
| `${table_name}`      | The name of the table this constraint affects             |
| `${column_names}`    | A comma-separated list of columns this constraint affects |

#### Not Null Constraints

Not Null Constraints are special in that they do not have a constraint name and only affect one
column:

| Placeholder      | Description                                             |
|------------------|---------------------------------------------------------|
| `${schema_name}` | The name of the schema the affected table is defined in |
| `${table_name}`  | The name of the table this constraint affects           |
| `${column_name}` | Name of the affected column                             |

#### Foreign Key Constraints

Due to their nature, foreign key constraints have different placeholders:

| Placeholder              | Description                                                                          |
|--------------------------|--------------------------------------------------------------------------------------|
| `${constraint_name}`     | The name of the constraint                                                           |
| `${parent_table_schema}` | The schema of the referenced table                                                   |
| `${parent_table_name}`   | The name of the referenced table                                                     |
| `${parent_column_names}` | A comma-separated list of referenced columns in the referenced table                 |
| `${child_table_schema}`  | The schema of the table the constraint is defined in                                 |
| `${child_table_name}`    | The name of the table the constraint is defined in                                   |
| `${child_column_names}`  | A comma-separated list of affected columns in the table the constraint is defined in |

### Constraint Strategies

Constraints can be defined with different strategies.
The strategy defines where the constraint is placed in the statement.

#### `END_OF_LINE`

Constraints with the strategy `END_OF_LINE` are placed at the end of the line of the column
definition.

#### `END_OF_BLOCK`

Constraints with the strategy `END_OF_BLOCK` are placed at the end of the block of column
definitions.

#### `STANDALONE`

Constraints with the strategy `STANDALONE` are placed as standalone statements.
They will be executed after all data has been transferred.
This is the default strategy and should be used wherever possible.

### Generation Strategies

GENERATED columns are columns that are automatically filled with a automatically generated value.
We support the following generation strategies:

**SERIAL:**
The column is automatically incremented by a sequence. If rows are deleted, the values are not
guaranteed to be unique (i.e. deleted values may be reused).

**IDENTITY:**
The column is automatically incremented by a sequence. If rows are deleted, the values are
guaranteed to be unique (i.e. deleted values will not be reused).

In the syntax you can define their templates separately. They behave like constraints in that they
also allow you to provide a strategy.

#### Placeholders

| Placeholder          | Description                                               |
|----------------------|-----------------------------------------------------------|
| `${constraint_name}` | The name of the constraint                                |
| `${schema_name}`     | The name of the schema the affected table is defined in   |
| `${table_name}`      | The name of the table this constraint affects             |
| `${column_names}`    | A comma-separated list of columns this constraint affects |

## Type Mappings

Type Mappings are for the conversion
of [JDBCTypes](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/JDBCType.html)
into vendor specific type declaration.
An example on how to use the Type-Mappings can be found
in [./src/main/resources/syntaxes/postgresql.xml](./src/main/resources/syntaxes/postgresql.xml).

If no type mapping for a type is specified, DBWarp will try to use the value of the `name()` method
of the JDBCType.

### Placeholders for Type-Mappings

For types with a size (e.g., VARCHAR(20)) you can use the `${column_size}` placeholder.

| Placeholder      | Description            |
|------------------|------------------------|
| `${column_size}` | The size of the column |

### Shortcomings

With the default type mappings, the most common types (VARCHAR, TEXT, INTEGER, DOUBLE, FLOAT,
BOOLEAN, DATE, TIME, TIMESTAMP) _should_ work sufficiently.
However, there is a major shortcoming in the default type mappings: The size of the VARCHAR type is
not considered.

This means that if you have a VARCHAR column with a size of 20 in your source database, it will be
migrated as a VARCHAR/TEXT without a size in the target database.
This is due to the fact that the maximum size of a VARCHAR column is not standardized across DBMS.

To fix this, you can define your own type mappings in the syntax file.

If we had had time to implement a more sophisticated type mapping, we would have implemented a way
to
define types with a minimum size and a maximum size and then choose the type with the smallest size
that fits the source type.

## Security considerations

### SQL Injection Attacks

The syntax file is only validated against the XML schema.
It is not validated against any security concerns as it is assumed as a trusted source.
Everything that is defined in the syntax file and everything injected through placeholders
will be executed as is.

Therefore, it is important that you only migrate from trusted source databases and
that you validate the syntax file before using it.

### XML External Entity Attacks

The XML parsers used to read and validate the syntax file are configured to disable external
entities.
This means that the syntax file cannot reference any external entities and is
therefore safe from XXE attacks.
