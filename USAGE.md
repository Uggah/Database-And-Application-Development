dbwarp

1

0.0.1-alpha

Dbwarp Manual

dbwarp

DBWarp is a cli application that allows you to migrate database schemas
between different database management systems. It is the product of our
semester project in the module Database and Application Development at
Stuttgart Media University, course Computer Science and Media (B.Sc.).

**dbwarp** \[**-hvV**\] \[**-vv**\] \[**-S**=*&lt;schema&gt;*\]
\[**-D**=*&lt;drivers&gt;*\]…​ \[**--syntax**=*&lt;String=String&gt;*\]…​
*&lt;source&gt;* *&lt;target&gt;*

# Description

DBWarp is a cli application that allows you to migrate database schemas
between different database management systems. It is the product of our
semester project in the module Database and Application Development at
Stuttgart Media University, course Computer Science and Media (B.Sc.).

# Options

**-D**, **--driver**=*&lt;drivers&gt;*  
Comma-separated paths to JAR-files containing JDBC drivers to load
dynamically

**-h**, **--help**  
Show this help message and exit.

**-S**, **--schema**=*&lt;schema&gt;*  
Schema to migrate, migrates all schemas by default

**--syntax**=*&lt;String=String&gt;*  
Syntaxes to use for migration. E.g. --syntax
SQLite=./syntaxes/custom\_sqlite.xml

**-v**, **--verbose**  
Verbose output

**-V**, **--version**  
Print version information and exit.

**-vv, --trace**  
Tracing output

# Arguments

*&lt;source&gt;*  
JDBC connection URL of source database

*&lt;target&gt;*  
JDBC connection URL of target database
