package com.momo.savanger.api.budget;

public interface BudgetService {

    Budget findBudgetById(long id);

    Budget saveBudget(BudgetDto budgetDto);
}
