alter table budgets
    add budget_name varchar(50) not null;
alter table categories
    add category_name varchar(50) not null;
alter table tags
    add tag_name varchar(50) not null;

update budgets
set budgets.budget_name = name
where 1 = 1;
update categories
set categories.category_name = name
where 1 = 1;
update tags
set tags.tag_name = name
where 1 = 1;

alter table budgets
drop
column name;
alter table categories
drop
column name;
alter table tags
drop
column name;

