create table budgets
(
    id             bigint auto_increment primary key,
    name           varchar(50)     not null,
    recurring_rule varchar(255)    not null,
    date_started   datetime        not null,
    due_date       datetime        not null,
    active         bit             not null,
    balance        decimal(30, 15) not null,
    budget_cap     decimal(30, 15),
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

create table revisions
(
    id                bigint auto_increment primary key,
    revision_date     datetime        not null,
    budget_start_date datetime        not null,
    balance           decimal(30, 15) not null,
    budget_cap        decimal(30, 15),
    expenses_amount   decimal(30, 15) not null,
    earnings_amount   decimal(30, 15) not null,
    auto_revise       bit             not null,
    comment           varchar(255),
    budget_id         bigint          not null
);

alter table revisions
    add constraint FK_Revisions_Budgets foreign key (budget_id) references budgets (id);

create table categories
(
    id         bigint auto_increment primary key,
    name       varchar(50) not null,
    budget_cap decimal(30, 15),
    budget_id  bigint      not null
);

alter table categories
    add constraint FK_Categories_Budgets foreign key (budget_id) references budgets (id);

create table transactions
(
    id          bigint auto_increment primary key,
    type        varchar(255)    not null,
    amount      decimal(30, 15) not null,
    date        datetime        not null,
    comment     varchar(255),
    revised     bit             not null,
    user_id     bigint          not null,
    category_id bigint          not null,
    budget_id   bigint          not null
);

alter table transactions
    add constraint FK_Transactions_Users foreign key (user_id) references users (id);

alter table transactions
    add constraint FK_Transactions_Categories foreign key (category_id) references categories (id);

alter table transactions
    add constraint FK_Transactions_Budgets foreign key (budget_id) references budgets (id);

create table tags
(
    id         bigint auto_increment primary key,
    name       varchar(50) not null,
    budget_cap decimal(30, 15),
    budget_id  bigint      not null
);

alter table tags
    add constraint FK_Tags_Budgets foreign key (budget_id) references budgets (id);
