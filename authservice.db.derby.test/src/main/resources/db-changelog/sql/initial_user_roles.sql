--liquibase formatted sql
--changeset sb:initial_user_roles
insert into user_roles (role_name, username) values ('admin','admin');
insert into user_roles (role_name, username) values ('admin','on');
insert into user_roles (role_name, username) values ('admin','kn');
insert into user_roles (role_name, username) values ('caseworker','on');
insert into user_roles (role_name, username) values ('caseworker','jad');
insert into user_roles (role_name, username) values ('caseworker','jod');
insert into user_roles (role_name, username) values ('useradmin','admin');
--rollback delete from user_roles; alter table user_role alter user_role_id restart with 1;
