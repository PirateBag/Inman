create table if not exists Item (
    Id int auto_increment,
    summary_Id varchar( 10 ) unique not null,
    description varchar(30 ) not null,

    /*  "PUR" or "MAN" */
    sourcing varchar( 3 ) default 'PUR' not null,

    max_depth int default 0 not null,

    unit_Cost number( 12, 2) default 0.0 not null,

    lead_time int default 1 not null,

    quantity_on_hand number( 12, 2) default 0.0 not null,

    minimum_order_quantity number( 12,2 ) default 1.0 not null
);

create table if not exists Bom (
    id int auto_increment,
    parent_Id int not null,
    child_Id int not null,
    quantity_per number (12, 2) default 0.0 not null,

    CONSTRAINT PK_BOM PRIMARY KEY ( PARENT_ID, CHILD_id)

);

create view if not exists  bom_Present as
    select b.id, b.QUANTITY_PER, b.parent_id, p.summary_id parent_summary, p.description parent_description,
    b.child_id, c.summary_id child_summary, c.description child_description, c.max_depth as max_depth,
    c.unit_cost unit_cost, c.unit_cost * b.quantity_per extended_cost
    from item p, item c, bom b where b.parent_id = p.id and b.child_id = c.id;

CREATE TABLE IF NOT EXISTS order_line_Item (
    id int auto_increment,
    start_date varchar( 10 ),
    complete_date varchar( 10 ),
    item_id int,
    order_state varchar(12),
    order_type varchar(5),
    parent_oli_id int,
    quantity_assigned number( 12,2) default 0.0,
    quantity_ordered number (12,2) default 0.0,

    CONSTRAINT PK_ORDER_LINE_ITEM PRIMARY KEY ( ID ) );

CREATE TABLE IF NOT EXISTS adjustment (
    id int auto_increment,
    amount number( 12,2),
    item_id int not null,
    order_id int,
    order_type varchar(5),
    effective_date varchar( 10 ) not null,
    adjustment_type varchar(4) not null,
    CONSTRAINT PK_ADJUSTMENT PRIMARY KEY ( ID ) );

