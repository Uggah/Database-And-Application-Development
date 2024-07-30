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

INSERT INTO owner (id, full_name, birth_date, address)
VALUES (1, 'Alice', '1990-01-01', 'Wonderland');
INSERT INTO owner (id, full_name, birth_date, address)
VALUES (2, 'Bob', '1990-01-02', 'Bobsville');

INSERT INTO pet (id, name, birth_date, owner_id)
VALUES (1, 'Fluffy', '2010-01-01', 1);
INSERT INTO pet (id, name, birth_date, owner_id)
VALUES (2, 'Spot', '2010-01-02', 1);
INSERT INTO pet (id, name, birth_date, owner_id)
VALUES (3, 'Rex', '2010-01-03', 2);
INSERT INTO pet (id, name, birth_date, owner_id)
VALUES (4, 'Fido', '2010-01-04', 2);