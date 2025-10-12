alter table transactions
    add date_created datetime not null;

update transactions
set transactions.date_created = date
where 1 = 1;

alter table transactions
drop
column date;
