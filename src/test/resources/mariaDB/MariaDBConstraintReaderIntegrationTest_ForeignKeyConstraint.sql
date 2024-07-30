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
CREATE TABLE IF NOT EXISTS parentTable
(
    key1  INTEGER,
    key2  INTEGER,
    key3  INTEGER,
    value INTEGER,
    PRIMARY KEY (key1, key2, key3)
);

CREATE TABLE IF NOT EXISTS childTable
(
    idkey INTEGER,
    fkey1 INTEGER,
    fkey2 INTEGER,
    fkey3 INTEGER,
    value INTEGER,
    PRIMARY KEY (idkey),
    FOREIGN KEY (fkey1, fkey2, fkey3) REFERENCES parentTable (key1, key2, key3)
);
