alter table transactions
    add debt_id bigint;

alter table transactions
    add constraint FK_Debts_Transactions foreign key (debt_id) references debts (id);
