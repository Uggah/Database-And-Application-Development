---
-- #%L
-- DBWarp
-- %%
-- Copyright (C) 2024 Kay Knöpfle, Lucca Greschner and contributors
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/gpl-3.0.html>.
-- #L%
---
CREATE TABLE test_table1
(
    id               int PRIMARY KEY,
    something_unique varchar(10) UNIQUE
);

CREATE TABLE test_table2
(
    id               int PRIMARY KEY,
    something_unique varchar(10),
    something_else   bool,
    UNIQUE (something_unique, something_else)
);

CREATE TABLE test_table3
(
    id               int PRIMARY KEY,
    something_unique varchar(10)
);

ALTER TABLE test_table3
    ADD CONSTRAINT custom_constraint_name UNIQUE (something_unique);
