INSERT INTO transactions(id, type, amount, date_created, comment, revised, user_id,
                         category_id, budget_id)
VALUES (1001, 'INCOME', 23.32, TIMESTAMP '2018-12-30 19:34:52',
        'Vaza', false, 1, 1001, 1001),
       (1002, 'INCOME', 100.00, TIMESTAMP '2025-04-30 19:34:52',
        'Hrana', false, 1, 1001, 1001),
       (1003, 'INCOME', 540.00, TIMESTAMP '2025-05-14 19:34:52',
        'dm', true, 1, 1002, 1001);
