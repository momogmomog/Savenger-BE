create table transactions
(
    id        bigint auto_increment primary key,
    type      varchar(255)    not null,
    amount    decimal(30, 15) not null,
    date      datetime        not null,
    comment   varchar(255),
    revised   bit             not null,
    user_id   bigint          not null,
    budget_id bigint          not null
);

alter table transactions
    add constraint FK_Transactions_Users foreign key (user_id) references users (id);

alter table transactions
    add constraint Fk_Transactions_Budgets foreign key (budget_id) references budgets (id);