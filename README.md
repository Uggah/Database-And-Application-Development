# DBWarp

DBWarp is a cli application that allows you to migrate database schemas
between different database management systems. It is the product of our
semester project in the module Database and Application Development at
Stuttgart Media University, course Computer Science and Media (B.Sc.).

# Usage

See [USAGE.md](./USAGE.md)

_NOTE: If you use a JAR file to run the application you must replace the DBWarp command
with `java -jar <PATH_TO_JAR_FILE> ...`_

## Features

Our goal with this application was to be able to migrate as many constraints as possible.
However, migrating all constraints is not possible due to the differences in the SQL dialects.
Therefore, DBWarp only offers a selection of constraints that can be migrated.

The following table shows the features that are supported by DBMS (Database Management System).

| Feature                                                        | PostgreSQL | SQLite | MariaDB | MySQL(**) |
|----------------------------------------------------------------|------------|--------|---------|-----------|
| Database Creation (no constraints, column generation, ...)     | ✅          | ✅      | ✅       | ❌         |
| Primary Key Constraints                                        | ✅          | ✅      | ✅       | ❌         |
| Foreign Key Constraints                                        | ✅          | ✅      | ✅       | ❌         |
| Unique Constraints                                             | ✅          | ✅      | ✅       | ❌         |
| Not Null Constraints                                           | ✅          | ✅      | ✅       | ❌         |
| Check Constraints                                              | ❌          | ❌      | ❌       | ❌         |
| Column Generation                                              | ✳️         | ✳️     | ✳️      | ❌         |
| - Auto Increment                                               | ✅          | ✅(*)   | ✅       | ❌         |
| - Default Values                                               | ✅          | ✅      | ✅       | ❌         |
| - Generation from functions (e.g. `DEFAULT gen_random_uuid()`) | ❌          | ❌      | ❌       | ❌         |

A cross (❌) means, that the feature is completely unsupported.

A checkmark (✅) means, that the feature is fully supported.

A star (✳️) means, that the feature is partially supported.

(**) MySQL is unsupported and untested. However, it will _probably_ work when using a custom JDBC driver and loading the included MariaDB schema as a custom schema for MySQL.

### Notable exceptions

#### Indexes

(Non-Unique) Indexes are not migrated as we do not consider them migratable.
The implementation of them vastly differ between DBMS.
So, an index created in PostgreSQL might not have the same positive effect,
or in many cases even a negative effect, in SQLite and vice versa.

As always, do only create the indexes that are obviously needed and add
more indexes whenever you see a performance issue.

#### Triggers (and other procedural code)

Triggers and other procedural code are not migrated as they are not
part of the schema but rather part of the database logic. We recommend
to manually migrate the procedural code.

#### Check Constraints

Migrating check constraints has not been implemented yet (and probably will never be implemented).
The reason for this is that a) check constraints' expressions are highly dependent on the underlying
DBMS
and b) there is no unified way to retrieve check constraints from the database.

For example, in SQLite the only possibility would be to manually parse the `CREATE TABLE` statement
from the `sqlite_master` table and extract the check constraints from there.

#### Column Generation

Column generation is partially supported.
While default values and automatic incrementation are migrated, generation from functions is not.

**Special case for SQLite:**

Writing auto incrementing columns to SQLite is not enabled by default. This is due to the way auto
incrementing works with SQLite. In SQLite all columns declared as `INTEGER PRIMARY KEY` are
automatically
incrementing. However, when adding the `AUTOINCREMENT` keyword, the column's value is guaranteed to
be
unique even if a column has previously been deleted.

As SQLite only supports auto incrementing primary keys, and we cannot assume that the user wants all
auto incrementing column to be a primary key (this would not even be possible),
we decided to not migrate explicit auto incrementing to SQLite.

Keep in mind that implicit auto incrementing as implemented by SQLite will still be in effect.
So, if all your auto incrementing columns are primary keys, you probably do not have to worry about
this.
If you however desire to include the `AUTOINCREMENT` keyword in your migrated SQLite schema,
you can define your own syntax definition including the following:

```xml
<syntax>
  <!-- ... -->
  <columnDefinition>
    ${column_name} ${column_type} ${column_generation} ${column_default} ${end_of_line_constraints}
  </columnDefinition>
  <generateIdentity strategy="END_OF_LINE">
    <!-- 
        This will be applied at the end of each column definition where
        the column is auto incrementing and the source database
        was configured so that the auto generated value will be unique
        even among deleted rows.  
    -->
    AUTOINCREMENT
  </generateIdentity>
  <!-- ... -->
</syntax>
```

#### Migration with MariaDB

In MariaDB, database, schema and catalog (in the sense of the JDBC driver) are all synonyms.
Therefore, when migrating a schema to MariaDB, the schema name will be used as the database name.

If you only migrate a single schema, you can just give the accessing user access to the target
database.
However, if you are migrating multiple schemas, you will have to use a user that has the rights to
access multiple databases (e.g., `root`).

#### Special types

Some types are not supported by all DBMS. For example, PostgreSQL has a `ARRAY` type, which is not
supported by MariaDB.
When there is no equivalent type in the target DBMS, the type will not be migrated by default.
You can try to define your own [syntax](./SYNTAX.md) definition with a matching type mapping to
migrate the type to a similar type in the target DBMS.

Also, custom
types ([PostgreSQL: CREATE TYPE](https://www.postgresql.org/docs/current/sql-createtype.html)) are
not supported at all.

## Loading own JDBC drivers

To load your own JDBC drivers, use the `-D` or `--driver` option.
The option takes a comma-separated list of paths to JAR files containing the JDBC drivers.
The drivers will be loaded dynamically at runtime.

Please note that the drivers may not collide with a driver already shaded into the JAR file.
If a driver is already shaded into the JAR file,
it will not be loaded again, but the shaded version will be used nonetheless.
To use a different version of the driver, you have to build the JAR file with the driver removed
from the maven dependencies specified in the `pom.xml`.

# Building

## Prerequisites

To build this project you'll need to have Maven and Java 21 installed.
We recommend to use Maven 3.9 or newer.

### Fedora

```shell
sudo dnf install -y maven-openjdk21
```

## JAR

To build a JAR file use the following command:

```shell
mvn package
```

You'll find the packaged JAR file at `./target/DBWarp-<version>.jar`

# Testing

To run all tests, use the following command:

```shell
mvn verify
```

# Documentation

## JavaDoc

To build the javadoc documentation, run the following command:

```shell
mvn javadoc:javadoc
```

If you desire to include private fields and methods in the generated documentation, use the
following command:

```shell
mvn javadoc:javadoc -Dshow=private
```

## Usage documentation

To generate a usage documentation, use the following commands:

```shell
mvn process-classes
```

This will generate the following formats:

- asciidoc (`./target/generated-adocs/dbwarp.adoc`)
- manpage (`./target/generated-docs/dbwarp.1`)
- html (`./target/generated-docs/dbwarp.html`)
- docbook (`./target/generated-docs/dbwarp.xml`)

### Markdown

The generated usage documentation can also be converted to markdown using external utilities.
This method is used to generate [USAGE.md](./USAGE.md).

```shell
pandoc -f docbook -t markdown_strict ./target/generated-docs/dbwarp.xml -o ./target/generated-docs/dbwarp.md
```

## Third Party Licenses

To generate a list of all third party licenses, use the following command:

```shell
mvn generate-sources
```

This goal will automatically be executed in build lifecycle and its result will be
included in the final
JAR (`META-INF/DBWarp-THIRD-PARTY.txt`, `META-INF/licenses.xml`, `META-INF/licenses`).
You can preview the files in `./target/generated-sources/licenses`.

## Architecture documentation

A small architecture documentation can be found in `./docs/architecture`.
It can be built using the included `build.sh` bash script.

For this script to work, it is required to have podman ([Installation Instructions](https://podman.io/docs/installation)) installed.
If you desire to use docker instead, you will find instructions
on how to modify the script to run on docker inside it.

# Copyright

(c) Kay Knöpfle, Lucca Greschner and contributors

SPDX-License-Identifier: GPL-3.0-or-later
