--liquibase formatted sql
--changeset sb:locked_user
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users where username in ('lu')
insert into users (username,password,password_salt,email,firstname,lastname,failed_login_count,is_locked) values ('lu','6VuUrsvVkZfxtKwt4tdCmOdXtXCuIbgWhcURzeRpT/g=', 'snKBcs4FMoGZHQlNY2kz5w==','lockeduser@gmail.com','Locked','User',3,true);
--rollback delete from users; alter table users alter user_id restart with 1;
