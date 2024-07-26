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
CREATE TABLE owner
(
    id         INTEGER PRIMARY KEY,
    full_name  TEXT,
    birth_date TEXT,
    address    TEXT,
    UNIQUE (full_name, birth_date)
);

CREATE TABLE pet
(
    id         INTEGER PRIMARY KEY,
    name       TEXT,
    birth_date TEXT,
    owner_id   INTEGER NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES owner (id)
);

