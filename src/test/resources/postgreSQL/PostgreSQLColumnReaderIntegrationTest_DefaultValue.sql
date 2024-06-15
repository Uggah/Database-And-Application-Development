CREATE TABLE person
(
    id   int primary key,
    name varchar(32) not null default 'John Doe'
);

CREATE TABLE uuidDefaultValue
(
    id   int primary key,
    name uuid not null default '00000000-0000-0000-0000-000000000000'
);

CREATE TABLE generatedDefault
(
    id   int primary key,
    name uuid not null default gen_random_uuid()
);