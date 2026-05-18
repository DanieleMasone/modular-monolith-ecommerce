create table catalog_products (
    id bigint primary key,
    sku varchar(100) not null unique,
    name varchar(255) not null,
    price numeric(12, 2) not null,
    available_quantity integer not null check (available_quantity >= 0),
    version bigint not null default 0
);

create table customer_orders (
    id uuid primary key,
    product_id bigint not null,
    quantity integer not null check (quantity > 0),
    status varchar(40) not null,
    created_at timestamp with time zone not null,
    constraint fk_customer_orders_product foreign key (product_id) references catalog_products (id)
);

create table payment_attempts (
    id uuid primary key,
    order_id uuid not null unique,
    product_id bigint not null,
    quantity integer not null check (quantity > 0),
    status varchar(40) not null,
    requested_at timestamp with time zone not null,
    constraint fk_payment_attempts_order foreign key (order_id) references customer_orders (id),
    constraint fk_payment_attempts_product foreign key (product_id) references catalog_products (id)
);

insert into catalog_products (id, sku, name, price, available_quantity)
values
    (1, 'SKU-IPHONE-15', 'iPhone 15', 899.00, 10),
    (2, 'SKU-MACBOOK-AIR', 'MacBook Air', 1299.00, 5),
    (3, 'SKU-KEYBOARD-MX', 'Mechanical Keyboard', 149.00, 20);
