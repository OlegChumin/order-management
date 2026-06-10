create table orders (
    id uuid not null,
    version bigint not null,
    customer_id uuid not null,
    status varchar(64) not null,
    inventory_reserved boolean not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint pk_orders primary key (id)
);

create table order_lines (
    id uuid not null,
    version bigint not null,
    order_id uuid not null,
    product_id uuid not null,
    quantity integer not null,
    unit_price_amount numeric(19, 2) not null,
    unit_price_currency varchar(3) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint pk_order_lines primary key (id),
    constraint fk_order_lines_order_id foreign key (order_id) references orders (id) on delete cascade
);

create index idx_orders_customer_id on orders (customer_id);
create index idx_orders_status on orders (status);
create index idx_order_lines_order_id on order_lines (order_id);
create index idx_order_lines_product_id on order_lines (product_id);
