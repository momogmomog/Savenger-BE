INSERT INTO recurring_transactions(id, type, recurring_rule, create_date, update_date, amount,
                                   next_date, auto_execute, completed, prepayment_id, category_id,
                                   budget_id, debt_id)
VALUES (1001, 'INCOME', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2025-07-03 14:14:52',
        TIMESTAMP '2025-07-03 14:14:52', 10, TIMESTAMP '2025-08-03 14:14:52', false, false, 1001,
        1001, 1001, 101);