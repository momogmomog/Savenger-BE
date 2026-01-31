package com.momo.savanger.constants;

public class Endpoints {

    public static final String LOGIN = "/login";
    public static final String USER_DETAILS = "/user-details";
    public static final String OTHER_USER_DETAILS = "/user-details/{username}";
    public static final String BUDGETS = "/budgets";
    public static final String BUDGET = "/budgets/{id}";
    public static final String BUDGET_STATISTICS = "/budgets/{id}/statistics";
    public static final String BUDGET_SEARCH = "/budgets/search";
    public static final String CATEGORIES = "/categories";
    public static final String CATEGORIES_SEARCH = "/categories/search";
    public static final String TAGS = "/tags";
    public static final String TAGS_SEARCH = "/tags/search";
    public static final String PARTICIPANTS = "/budgets/{id}/participants";
    public static final String TRANSACTIONS = "/transactions";
    public static final String TRANSACTIONS_SEARCH = "/transactions/search";
    public static final String TRANSACTION = "transactions/{id}";
    public static final String REVISIONS = "/revisions";
    public static final String DEBTS = "/debts";
    public static final String PAY_DEBT = "/debts/{id}/pay";
    public static final String PREPAYMENTS = "/prepayments";
    public static final String PAY_R_TRANSACTION = "/recurring-transaction/{rTransactionId}/pay";
    public static final String TRANSFERS = "/transfers";
    public static final String TRANSFER = "/transfers/{id}";
    public static final String TRANSFERS_SEARCH = "/transfers/search";
    public static final String TRANSFER_TRANSACTIONS = "/transfers/transactions";
    public static final String TRANSFER_TRANSACTION = "/transfers/transactions/{id}";
    public static final String RECURRING_TRANSACTIONS = "/recurring-transactions";
    public static final String ANALYTICS_CATEGORIES = "/analytics/categories";
    public static final String ANALYTICS_TAGS = "/analytics/tags";
}
