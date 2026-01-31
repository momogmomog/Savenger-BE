INSERT INTO budgets(id, budget_name, recurring_rule, date_started, due_date, active, balance,
                    budget_cap, auto_revise, owner_id)
VALUES (1001, 'bochko', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2025-07-03 14:14:52',
        TIMESTAMP '2015-07-31 23:59:59', true, 321.00, 500, true, 1),
       (1002, 'Knigi', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2019-01-30 19:34:52',
        TIMESTAMP '2019-12-30 19:34:56', true, 243.00, 0, true, 1),
       (1003, 'Smetki', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2019-01-30 19:34:52',
        TIMESTAMP '2019-12-30 19:34:56', true, 500.00, 0, true, 3),
       (1004, 'Smetki', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2019-01-30 19:34:52',
        TIMESTAMP '2019-12-30 19:34:56', true, 500.00, 0, true, 3);

