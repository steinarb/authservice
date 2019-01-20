--liquibase formatted sql
--changeset sb:initial_roles
insert into roles (role_name, description) values ('admin','Administrate stuff');
insert into roles (role_name, description) values ('caseworker','Respond to cases');
--rollback delete from roles; alter table roles alter role_id restart with 1;
