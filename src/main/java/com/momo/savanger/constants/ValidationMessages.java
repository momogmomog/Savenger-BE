package com.momo.savanger.constants;

public class ValidationMessages {

    public static final String TEXT_MUST_BE_BETWEEN = "The text must be between {min} and {max} characters.";
    public static final String FIELD_CANNOT_BE_NULL = "Field cannot be null.";
    public static final String FIELD_IS_NULL_OR_INVALID = "Field is null or invalid.";
    public static final String INVALID_RRULE = "Invalid Recurring Rule format";
    public static final String USER_DOESNT_EXIST = "User does not exist.";
    public static final String OWNER_CANNOT_BE_EDIT = "Owner cannot be edit.";
    public static final String ALREADY_PARTICIPANT = "This user is already a participant.";
    public static final String PARTICIPANT_NOT_EXIST = "Participant does not exist.";
    public static final String INVALID_BUDGET = "Missing or invalid budget";
    public static final String CATEGORY_DOES_NOT_EXIST_OR_BUDGET_IS_NOT_VALID = "Category does not exist or budget is not valid.";
    public static final String TRANSACTION_IS_REVISED = "Transaction is already revised and cannot be edit.";
    public static final String INVALID_TRANSACTION = "Invalid transaction or you don't have access to it.";
    public static final String TRANSACTION_DOES_NOT_EXIST = "Transaction does not exist.";
    public static final String DEBT_ALREADY_EXIST = "Debt with these budgets already exist.";
    public static final String DEBT_IS_NOT_VALID = "Debt is not valid.";
    public static final String BUDGETS_SHOULD_BE_DIFFERENT = "Lender and receiver budget ids should be different.";
    public static final String BUDGETS_SHOULD_BE_EQUALS = "BudgetId in RTransaction should be equals to BudgetId in Prepayment";
    public static final String FIELDS_CANNOT_BE_NULL = "One of these fields %s cannot be null.";
    public static final String AMOUNT_CANNOT_BE_BIGGER_THAN_BALANCE = "Amount cannot be bigger than budget balance";

}
