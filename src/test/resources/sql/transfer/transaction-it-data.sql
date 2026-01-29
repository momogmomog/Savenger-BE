INSERT INTO transactions(id, type, amount, date_created, comment, revised, user_id,
                         category_id, budget_id, prepayment_id, transfer_transaction_id)
VALUES (203, 'EXPENSE', 1.0, TIMESTAMP '2018-12-30 19:34:52',
        'Migla', false, 1, 302, 1001, null, null),
       (204, 'EXPENSE', 50, TIMESTAMP '2026-01-27 11:06:43',
        'Komentar', false, 1, 302, 1001, null, 594),
       (205, 'INCOME', 50, TIMESTAMP '2018-12-30 19:34:52',
        'Migla', false, 1, 303, 1002, null, 594);

