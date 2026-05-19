alter table customer_orders
    add column idempotency_key varchar(128);

create unique index ux_customer_orders_idempotency_key
    on customer_orders (idempotency_key)
    where idempotency_key is not null;
