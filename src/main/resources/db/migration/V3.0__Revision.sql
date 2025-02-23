create table revisions
(
    id                bigint auto_increment primary key,
    revision_date     datetime        not null,
    budget_start_date datetime        not null,
    balance           decimal(30, 15) not null,
    budget_cap        decimal(30, 15),
    expenses_amount   decimal(30, 15) not null,
    earnings_amount   decimal(30, 15) not null,
    auto_revise       bit             not null,
    comment           varchar(255),
    budget_id         bigint          not null
);

alter table revisions
    add constraint FK_Revisions_Budgets foreign key (budget_id) references budgets (id);