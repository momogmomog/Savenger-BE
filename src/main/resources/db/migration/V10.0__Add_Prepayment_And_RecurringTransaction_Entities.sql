create table prepayments
(
    id               bigint auto_increment primary key,
    amount           decimal(30, 15) not null,
    name             varchar(50)     not null,
    create_date      datetime        not null,
    update_date      datetime        not null,
    paid_until       datetime        not null,
    completed        bit             not null,
    remaining_amount decimal(30, 15) not null,
    budget_id        bigint          not null
);

alter table prepayments
    add constraint FK_Prepayments_Budgets foreign key (budget_id) references budgets (id);

create table recurring_transactions
(
    id             bigint auto_increment primary key,
    type           varchar(255)    not null,
    recurring_rule varchar(255)    not null,
    create_date    datetime        not null,
    update_date    datetime        not null,
    amount         decimal(30, 15) not null,
    next_date      datetime        not null,
    auto_execute   bit             not null,
    completed      bit             not null,
    prepayment_id  bigint,
    category_id    bigint,
    budget_id      bigint          not null,
    debt_id        bigint

);

alter table recurring_transactions
    add constraint FK_Recurring_Transactions_Prepayments foreign key (prepayment_id) references prepayments (id);

alter table recurring_transactions
    add constraint FK_Recurring_Transactions_Categories foreign key (category_id) references categories (id);

alter table recurring_transactions
    add constraint FK_Recurring_Transactions_Budgets foreign key (budget_id) references budgets (id);

alter table recurring_transactions
    add constraint FK_Recurring_Transactions_Debts foreign key (debt_id) references debts (id);


create table recurring_transactions_tags
(
    recurring_transaction_id bigint,
    tag_id                   bigint
);

alter table recurring_transactions_tags
    add constraint FK_Recurring_Transactions_Tags_Transactions
        foreign key (recurring_transaction_id) references recurring_transactions (id) on delete cascade;

alter table recurring_transactions_tags
    add constraint FK_Recurring_Transactions_Tags_Tags
        foreign key (tag_id) references tags (id) on delete cascade;
