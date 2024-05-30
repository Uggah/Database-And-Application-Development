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

ALTER TABLE ONLY test_table3
    ADD CONSTRAINT custom_constraint_name UNIQUE (something_unique);