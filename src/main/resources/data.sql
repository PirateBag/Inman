insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-001', '36 in red wagon', '0.0', 'MAN' );
insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-002', 'red painted wagon body', '0.0', 'MAN' );
insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-003', 'Front Wheel assembly', '0.0', 'MAN' );
insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-004', '24 in handle', '0.4', 'PUR' );
insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-005', '8 in wheel', '0.5', 'PUR' );
insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-006', 'Rear Wheel assembly', '0.0', 'MAN' );
insert into Item ( summary_id, description, unit_cost, sourcing )
values  ('W-007', 'Rear wheel bracket', '0.7', 'MAN' );

insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,2,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,3,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 3,4,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 3,5,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 1,6,1.0 );
insert into Bom ( parent_Id, child_id, quantity_per ) values ( 6,7,1.0 );