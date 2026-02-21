alter table recurring_transactions
    add column comment varchar(255) null,
    add column occurrences int null,
    add column start_from datetime null,
    add column include_in_balance boolean null;

update recurring_transactions
set occurrences        = 0,
    include_in_balance = false,
    start_from         = now()
where occurrences is null;

alter table recurring_transactions
    modify column occurrences int not null default 0,
    modify column start_from datetime not null,
    modify column include_in_balance boolean not null default false;
