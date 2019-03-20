--liquibase formatted sql
--changeset sb:initial_users
insert into users (username,password,password_salt,email,firstname,lastname) values ('admin','e0/CP6ewTISoWCNfJtUNw/vHTEWl6lXlXGjmE+hBH1o=', 'Iu9wre2JgXuxvRT2MA0CGQ==','admin@localhost','Admin','Istrator');
--rollback delete from users; alter table users alter user_id restart with 1;
