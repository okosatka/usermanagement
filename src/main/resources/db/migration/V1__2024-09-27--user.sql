-- create user table
create table tb_user
(
    username varchar(255) primary key,
    password varchar(255) not null,
    email    varchar(320) not null
);
comment on table tb_user is 'All users';

-- create user external project table
create table tb_user_external_project
(
    project_id varchar(200) not null,
    username   varchar(255) not null,
    name       varchar(255) not null,
    primary key (username, project_id),
    foreign key (username) references tb_user (username)
);
comment on table tb_user_external_project is 'External Project identifier for users';