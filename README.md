# DBWarp

DBWarp is a cli application that allows you to migrate database schemas
between different database management systems. It is the product of our
semester project in the module Database and Application Development at
Stuttgart Media University, course Computer Science and Media (B.Sc.).

# Usage

See [USAGE.md](./USAGE.md)

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

# Copyright

(c) Kay Knöpfle, Lucca Greschner and contributors

SPDX-License-Identifier: GPL-3.0-or-later