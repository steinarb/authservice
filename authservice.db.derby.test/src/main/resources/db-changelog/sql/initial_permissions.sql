--liquibase formatted sql
--changeset sb:initial_permissions
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM permissions
insert into permissions (permission_name, description) values ('user_admin_api_read','User admin read access');
insert into permissions (permission_name, description) values ('user_admin_api_write','User admin write access');
insert into permissions (permission_name, description) values ('caseworker_read','Caseworker read access');
insert into permissions (permission_name, description) values ('caseworker_write','Caseworker write access');
insert into permissions (permission_name, description) values ('user_read','User read access');
--rollback delete from permissions; alter table permissions alter permission_id restart with 1;
