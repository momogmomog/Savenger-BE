create table debts
(
    id                 bigint auto_increment primary key,
    receiver_budget_id bigint   not null,
    lender_budget_id   bigint   not null,
    amount             decimal(30, 15),
    create_date        datetime not null,
    update_date        datetime not null
);

alter table debts
    add constraint FK_Debts_ReceiverBudget foreign key (receiver_budget_id) references budgets (id),

    add constraint FK_Debts_LenderBudget foreign key (lender_budget_id) references budgets (id);