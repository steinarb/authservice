--liquibase formatted sql
--changeset sb:initial_roles
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM roles where role_name in ('admin', 'caseworker', 'visitor')
insert into roles (role_name, description) values ('admin','Administrate stuff');
insert into roles (role_name, description) values ('caseworker','Respond to cases');
insert into roles (role_name, description) values ('visitor','Just browsing');
--rollback delete from roles; alter table roles alter role_id restart with 1;
