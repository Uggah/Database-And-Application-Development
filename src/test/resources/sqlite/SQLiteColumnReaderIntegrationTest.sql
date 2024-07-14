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
CREATE TABLE Person
(
    id   int PRIMARY KEY,
    name text NOT NULL,
    age  int  NOT NULL
);

CREATE TABLE IntDefaultValue
(
    id                 int PRIMARY KEY,
    with_default_value int DEFAULT 1
);

CREATE TABLE RealDefaultValue
(
    id                 int PRIMARY KEY,
    with_default_value real DEFAULT 1.0
);

CREATE TABLE TextDefaultValue
(
    id                 int PRIMARY KEY,
    with_default_value text DEFAULT 'SomeDefaultValue'
);
