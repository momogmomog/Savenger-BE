alter table transactions
    add prepayment_id bigint;

alter table transactions
    add constraint FK_Prepayments_Transactions foreign key (prepayment_id) references prepayments (id);
