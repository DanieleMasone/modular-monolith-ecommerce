create table catalog_products (
                                  id bigint primary key,
                                  sku varchar(100) not null unique,
                                  name varchar(255) not null,
                                  price numeric(12, 2) not null,
                                  available_quantity integer not null
);

create table orders (
                        id uuid primary key,
                        product_id bigint not null,
                        quantity integer not null,
                        status varchar(50) not null,
                        created_at timestamp with time zone not null
);

insert into catalog_products (id, sku, name, price, available_quantity)
values
    (1, 'SKU-IPHONE-15', 'iPhone 15', 899.00, 10),
    (2, 'SKU-MACBOOK-AIR', 'MacBook Air', 1299.00, 5),
    (3, 'SKU-KEYBOARD-MX', 'Mechanical Keyboard', 149.00, 20);