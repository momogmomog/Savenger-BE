create table categories
(
    id         bigint auto_increment primary key,
    name       varchar(50)     not null,
    budget_cap decimal(30, 15) not null,
    budget_id  bigint          not null
);

alter table categories
    add constraint FK_Categories_Budgets foreign key (budget_id) references budgets (id);