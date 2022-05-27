DROP view bom_Present;
DROP TABLE Item;
DROP tABLE BOM;

create table Item  (
                       id identity,
                       summary_id varchar(10),
                       description varchar(30),
                       unit_cost double,
                       UNIQUE( summary_id )
);

create table Bom (
                     id identity,
                     parent_id integer(10),
                     child_id integer(10),
                     quantity_per double,
                     UNIQUE( parent_id, child_id )
);

create view bom_Present as select b.id, b.QUANTITY_PER, b.parent_id, p.summary_id parent_summary, b.child_id, c.summary_id child_summary  from item p, item c, bom b where b.parent_id = p.id and b.child_id = c.id;

delete from item;
insert into Item ( summary_id, description, unit_cost ) values  ('W-001', '36 in red wagon', '0.0' );
insert into Item ( summary_id, description, unit_cost ) values  ('W-002', 'red painted wagon body', '0.0' );
insert into Item ( summary_id, description, unit_cost ) values  ('W-003', 'Front Wheel assembly', '0.0' );
insert into Item ( summary_id, description, unit_cost ) values  ('W-004', '24 in handle', '0.4' );
insert into Item ( summary_id, description, unit_cost ) values  ('W-005', '8 in wheel', '0.5' );
insert into Item ( summary_id, description, unit_cost ) values  ('W-006', 'Rear Wheel assembly', '0.0' );
insert into Item ( summary_id, description, unit_cost ) values  ('W-007', 'Rear wheel brakent', '0.7' );

delete from bom;
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,2,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,3,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 3,4,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 3,5,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,6,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 6,7,1.0 );

