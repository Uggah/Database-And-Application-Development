CREATE TABLE IF NOT EXISTS "parentTable"
(
    "key1"
    INTEGER,
    "key2"
    INTEGER,
    "key3"
    INTEGER,
    "value"
    INTEGER,
    PRIMARY
    KEY
(
    "key1",
    "key2",
    "key3"
)
    );
CREATE TABLE IF NOT EXISTS "childTable"
(
    "key"
    INTEGER,
    "fkey1"
    INTEGER,
    "fkey2"
    INTEGER,
    "fkey3"
    INTEGER,
    "value"
    INTEGER,
    PRIMARY
    KEY
(
    "key"
),
    FOREIGN KEY
(
    "fkey1",
    "fkey2",
    "fkey3"
) REFERENCES "parentTable"
(
    "key1",
    "key2",
    "key3"
)
    );

