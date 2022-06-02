create table if not exists Item (
    Id int auto_increment,
    summary_Id varchar( 10 ),
    description varchar(30 ),

    /*  "PUR" or "MAN" */
    sourcing varchar( 3 ),

    unit_Cost number( 12, 2)
);

create table if not exists Bom (
    id int auto_increment,
    parent_Id int,
    child_Id int,
    quantity_per number (12, 2)
);

create view if not exists  bom_Present as select b.id, b.QUANTITY_PER, b.parent_id, p.summary_id parent_summary,
    b.child_id, c.summary_id child_summary, c.description child_description  from item p, item c, bom b where b.parent_id = p.id and b.child_id = c.id;