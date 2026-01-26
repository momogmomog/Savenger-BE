create table transfers
(
    id                 bigint auto_increment primary key,
    source_budget_id   bigint not null,
    receiver_budget_id bigint not null,
    active             bit    not null
);

create unique index idx_unique_transfer
    on transfers (source_budget_id, receiver_budget_id);

alter table transfers
    add constraint FK_Transfers_SourceBudget foreign key (source_budget_id) references budgets (id);

alter table transfers
    add constraint FK_Transfers_ReceiverBudget foreign key (receiver_budget_id) references budgets (id);

create table transfer_transactions
(
    id          bigint auto_increment primary key,
    transfer_id bigint not null
);

alter table transfer_transactions
    add constraint FK_Transfer_Transactions_Transfers foreign key (transfer_id) references transfers (id);

alter table transactions
    add transfer_transaction_id bigint;

alter table transactions
    add constraint FK_Transactions_Transfer_Transactions foreign key (transfer_transaction_id)
        references transfer_transactions (id);
