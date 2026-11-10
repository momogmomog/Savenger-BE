alter table transactions
    add recurring_transaction_id bigint;

update transactions t
    join recurring_transactions rt
on t.prepayment_id = rt.prepayment_id
    set t.recurring_transaction_id = rt.id
where t.prepayment_id is not null;

alter table transactions
    add constraint FK_RecurringTransactions_Transactions foreign key (recurring_transaction_id) references recurring_transactions (id);
