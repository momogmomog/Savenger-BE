create table transactions_tags
(
    transaction_id bigint,
    tag_id         bigint
);

alter table transactions_tags
    add constraint FK_Transactions_Tags_Transactions
        foreign key (transaction_id) references transactions (id) on delete cascade;

alter table transactions_tags
    add constraint FK_Transactions_Tags_Tags
        foreign key (tag_id) references tags (id) on delete cascade;
