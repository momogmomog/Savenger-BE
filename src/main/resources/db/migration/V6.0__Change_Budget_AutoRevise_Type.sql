alter table budgets
    add is_auto_revised boolean;

update budgets
set budgets.is_auto_revised = auto_revise
where 1 = 1;

alter table budgets
drop
column auto_revise;

alter table budgets
    add auto_revise bit;

update budgets
set budgets.auto_revise = is_auto_revised
where 1 = 1;

alter table budgets
drop
column is_auto_revised;

