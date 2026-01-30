INSERT INTO transactions(id, type, amount, date_created, comment, revised, user_id,
                         category_id, budget_id, transfer_transaction_id)
VALUES (205, 'INCOME', 50, TIMESTAMP '2018-12-30 19:34:52',
        'Migla', false, 1, 304, 1002, 594),
       (206, 'EXPENSE', 50, TIMESTAMP '2018-12-30 19:34:52',
        'Migla', false, 1, 302, 1001, 594);
