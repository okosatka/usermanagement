-- we will need to add a role to the table (but in a real world scenario,
-- we would have a separate tables for roles and permissions)
alter table tb_user
    add column role varchar(255) not null default 'USER';


-- we will need to add an admin user to the table (admin:password)
insert into tb_user (username, password, email, role)
values ('admin', '{bcrypt}$2a$10$uS77k5RNR6No6Nc3HE.C7uy1906TcFiBQE90z9a.5lH7kum2fS53a', 'admin@home.com', 'ADMIN');