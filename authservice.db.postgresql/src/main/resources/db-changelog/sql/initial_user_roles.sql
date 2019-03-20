--liquibase formatted sql
--changeset sb:initial_user_roles
insert into user_roles (role_name, username) values ('useradmin','admin');
--rollback delete from user_roles; alter table user_role alter user_role_id restart with 1;
