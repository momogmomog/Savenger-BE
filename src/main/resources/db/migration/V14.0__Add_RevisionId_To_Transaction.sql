alter table transactions
    add revision_id bigint;

update transactions t
    join revisions r
on t.revised = true
    and t.budget_id = r.budget_id
    and t.date_created between r.budget_start_date and r.revision_date
    set t.revision_id = r.id;

alter table transactions
    add constraint FK_Revisions_Transactions foreign key (revision_id) references revisions (id);