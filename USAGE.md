# DBWarp Manual

DBWarp is a cli application that allows you to migrate database schemas
between different database management systems. It is the product of our
semester project in the module Database and Application Development at
Stuttgart Media University, course Computer Science and Media (B.Sc.).

**dbwarp** \[**-v**\] \[**-vv**\]
\[**-D**=*&lt;drivers&gt;*\[,*&lt;drivers&gt;*…\]\]… *&lt;source&gt;*
*&lt;target&gt;*

# Description

DBWarp is a cli application that allows you to migrate database schemas
between different database management systems. It is the product of our
semester project in the module Database and Application Development at
Stuttgart Media University, course Computer Science and Media (B.Sc.).

# Options

**-D**, **--drivers**=*&lt;drivers&gt;*\[,*&lt;drivers&gt;*…\]  
Comma-separated paths to JAR-files containing JDBC drivers to load
dynamically

**-v**, **--verbose**  
Verbose output

**-vv, --trace**  
Tracing output

# Arguments

*&lt;source&gt;*  
JDBC connection URL of source database

*&lt;target&gt;*  
JDBC connection URL of target database
