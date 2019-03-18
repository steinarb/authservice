--liquibase formatted sql
--changeset sb:initial_roles
insert into roles (role_name, description) values ('admin','Administrate stuff');
insert into roles (role_name, description) values ('useradmin','Administrate users');
insert into roles (role_name, description) values ('caseworker','Respond to cases');
insert into roles (role_name, description) values ('visitor','Just browsing');
--rollback delete from roles; alter table roles alter role_id restart with 1;
