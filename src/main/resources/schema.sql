create table if not exists Item (
    Id int auto_increment,
    summary_Id varchar( 10 ) unique not null,
    description varchar(30 ) not null,

    /*  "PUR" or "MAN" */
    sourcing varchar( 3 ) default 'PUR' not null,

    max_depth int default 0 not null,

    unit_Cost number( 12, 2) default 0.0 not null

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

