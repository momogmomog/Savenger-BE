alter table transactions
    drop foreign key FK_Transactions_Categories;

alter table transactions
    add column category bigint;

update transactions
set category = category_id
where 1 = 1;

alter table transactions
    drop column category_id;

alter table transactions
    add column category_id bigint;

update transactions
set category_id = category
where 1 = 1;

alter table transactions
    drop column category;

alter table transactions
    add constraint FK_Transactions_Categories
        foreign key (category_id) references categories (id);

alter table transactions
    drop foreign key FK_Transactions_Users;

alter table transactions
    add column user_temp bigint;

update transactions
set user_temp = user_id
where 1 = 1;

alter table transactions
    drop column user_id;

alter table transactions
    add column user_id bigint;

update transactions
set user_id = user_temp
where 1 = 1;

alter table transactions
    drop column user_temp;

alter table transactions
    add constraint FK_Transactions_Users
        foreign key (user_id) references users (id);
