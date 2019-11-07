--liquibase formatted sql
--changeset sb:initial_roles_permissions
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM roles_permissions
insert into roles_permissions (role_name, permission_name) values ('admin','user_admin_api_read');
insert into roles_permissions (role_name, permission_name) values ('admin','user_admin_api_write');
insert into roles_permissions (role_name, permission_name) values ('admin','caseworker_read');
insert into roles_permissions (role_name, permission_name) values ('admin','caseworker_write');
insert into roles_permissions (role_name, permission_name) values ('caseworker','caseworker_read');
insert into roles_permissions (role_name, permission_name) values ('caseworker','caseworker_write');
--rollback delete from roles_permissions; alter table roles_permissions alter role_permission_id restart with 1;
