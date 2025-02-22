create table budgets
(
    id             bigint auto_increment primary key,
    name           varchar(50)     not null,
    recurring_rule varchar(255)    not null,
    date_started   datetime        not null,
    due_date       datetime        not null,
    active         bit             not null,
    balance        decimal(30, 15) not null,
    budget_cap     decimal(30, 15) not null,
    auto_revise    decimal(30, 15) not null,
    owner_id       bigint          not null

);

alter table budgets
    add constraint FK_Budgets_Users foreign key (owner_id) references users (id);

create table budgets_participants
(
    user_id   bigint,
    budget_id bigint
);

alter table budgets_participants
    add constraint FK_Budgets_Participants_Users
        foreign key (user_id) references users (id) on delete cascade;

alter table budgets_participants
    add constraint FK_Budgets_Participants_Budgets
        foreign key (budget_id) references budgets (id) on delete cascade;



