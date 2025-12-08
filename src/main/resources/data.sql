insert into Item (  description, unit_cost, sourcing )
values  ( '36 in red wagon', '0.0', 'MAN' );
insert into Item (  description, unit_cost, sourcing )
values  ( 'red painted wagon body', '0.0', 'MAN' );
insert into Item (  description, unit_cost, sourcing )
values  ('Front Wheel assembly', '0.0', 'MAN' );
insert into Item (  description, unit_cost, sourcing )
values  ( '24 in handle', '0.4', 'PUR' );
insert into Item (  description, unit_cost, sourcing )
values  ( '8 in wheel', '0.5', 'PUR' );
insert into Item (  description, unit_cost, sourcing )
values  ('Rear Wheel assembly', '0.0', 'MAN' );
insert into Item (  description, unit_cost, sourcing )
values  ('Rear wheel bracket', '0.7', 'MAN' );

insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,2,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,3,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 3,4,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 3,5,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,6,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 6,7,1.0 );