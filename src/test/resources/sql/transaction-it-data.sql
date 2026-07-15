INSERT INTO transactions(id, type, amount, date_created, comment, user_id,
                         category_id, budget_id, revision_id)
VALUES (1001, 'INCOME', 23.32, TIMESTAMP '2018-12-30 19:34:52',
        'Vaza', 1, 1001, 1001, 1001),
       (1002, 'INCOME', 100.00, TIMESTAMP '2025-04-30 19:34:52',
        'Hrana', 1, 1001, 1001, 1001),
       (1003, 'INCOME', 540.00, TIMESTAMP '2025-05-14 19:34:52',
        'dm', 1, 1002, 1001, null),
       (1004, 'EXPENSE', 45.00, TIMESTAMP '2025-06-16 16:32:54',
        'drehi', 1, 1001, 1001, null);
