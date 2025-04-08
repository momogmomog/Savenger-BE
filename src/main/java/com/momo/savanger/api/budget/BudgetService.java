package com.momo.savanger.api.budget;

import java.util.List;

public interface BudgetService {

    Budget findBudgetById(Long id);

    Budget saveBudget(CreateBudgetDto createBudgetDto, Long ownerId);

    List<Budget> findAllBudgets();
}
