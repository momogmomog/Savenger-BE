create table tags
(
    id         bigint auto_increment primary key,
    name       varchar(50)     not null,
    budget_cap decimal(30, 15) not null,
    budget_id  bigint          not null
);

alter table tags
    add constraint FK_Tags_Budgets foreign key (budget_id) references budgets (id);