create table users
(
    id              bigint auto_increment primary key,
    username        varchar(255) not null,
    password        varchar(255) not null,
    date_registered datetime     not null
);

create table auth_tokens
(
    id               varchar(255) primary key,
    user_id          bigint   not null,
    last_access_time datetime not null
);

alter table auth_tokens
    add constraint FK_Auth_Tokens_Users foreign key (user_id) references users (id) on delete cascade;
