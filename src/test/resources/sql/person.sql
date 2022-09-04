create table IF NOT EXISTS persons
(
    id         bigint PRIMARY KEY AUTO_INCREMENT,
    name       text      not null,
    age        int,
    created_at timestamp not null default CURRENT_TIMESTAMP()
);

INSERT INTO persons(name, age)
VALUES ('John', 21),
       ('Bilbo', 129)