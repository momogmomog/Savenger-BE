INSERT INTO transactions(id, type, amount, date_created, comment, revised, user_id,
                         category_id, budget_id)
VALUES (1001, 'EXPENSE', 1.0, TIMESTAMP '2018-12-30 19:34:52',
        'Migla', false, 1, 1001, 1001);

INSERT INTO transactions(id, type, amount, date_created, comment, revised, user_id,
                         category_id, budget_id)
VALUES (1002, 'EXPENSE', 1.0, TIMESTAMP '2018-12-30 19:34:52',
        'Migla', false, 1, 1001, 100111);
