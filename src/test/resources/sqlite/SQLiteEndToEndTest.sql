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

