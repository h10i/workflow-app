insert into account(id, email_address, password) values
  ('15101d3d-0b10-4e5a-aae1-b21ccdf06b34', 'test@example.com', '$2a$10$Ui1YMe5Zi3zTKElzouyAseR1gaDhrMaEyzNlcMqmpI9nAU.B8IhUe')
;

insert into role(id, name) values
  ('7189ff37-21e6-4ee5-a8c5-83282e3e1de1', 'USER'),
  ('3c21a2e0-7594-45bf-8eb3-6bf9edbed13d', 'ADMIN')
;

insert into account_role(id, account_id, role_id) values
  ('b227aa49-57bc-42a5-bfc2-10ecfc77fd40', '15101d3d-0b10-4e5a-aae1-b21ccdf06b34', '7189ff37-21e6-4ee5-a8c5-83282e3e1de1'),
  ('215961cb-d9e4-4a10-bfe3-dddc6714c125', '15101d3d-0b10-4e5a-aae1-b21ccdf06b34', '3c21a2e0-7594-45bf-8eb3-6bf9edbed13d')
;
