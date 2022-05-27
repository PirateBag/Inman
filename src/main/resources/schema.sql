

create table if not exists Item (
    Id int auto_increment,
    summary_Id varchar( 10 ),
    description varchar(30 ),
    unit_Cost number( 12, 2)
);

create table if not exists Bom (
    id int auto_increment,
    parent_Id int,
    child_Id int,
    quantity_per number (12, 2)
);

create view if not exists bom_Present as
   select b.id, b.parent_Id, b.child_Id, b.quantity_Per, p.summary_id as parent_Summary, c.summary_id as child_summary
    from Bom b, item p, item c
    where p.id= b.parent_id and c.id = b.child_id;
