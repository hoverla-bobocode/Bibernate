create table IF NOT EXISTS products
(
    id         bigint PRIMARY KEY AUTO_INCREMENT,
    name       text      not null,
    price      double,
    created_at timestamp not null default CURRENT_TIMESTAMP()
);

INSERT INTO products(name, price)
VALUES ('scissors', 1.00),
       ('rope', 10.00),
       ('knife', 5)