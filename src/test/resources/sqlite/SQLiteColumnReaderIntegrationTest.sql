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