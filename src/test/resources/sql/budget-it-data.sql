INSERT INTO budgets(id, budget_name, recurring_rule, date_started, due_date, active, balance,
                    budget_cap, auto_revise, owner_id)
VALUES (1001, 'Food', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2018-12-30 19:34:52',
        TIMESTAMP '2019-12-30 19:34:56', true, 23.00, 50, true, 1),
       (1002, 'sdf', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2019-01-30 19:34:52',
        TIMESTAMP '2019-12-30 19:34:56', true, 243.00, 0, true, 1),
       (1003, 'Food', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2023-12-30 19:34:52',
        TIMESTAMP '2023-12-30 19:34:56', true, 53.00, 43, false, 3),
       (1004, 'Home', 'FREQ=DAILY;INTERVAL=1', TIMESTAMP '2024-10-30 19:34:52',
        TIMESTAMP '2024-10-29 18:34:56', true, 53.00, 500, false, 1);

