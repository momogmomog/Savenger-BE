package com.momo.savanger.api.budget;

import com.momo.savanger.api.user.User;

public interface BudgetService {

    Budget findById(Long id);

    Budget create(CreateBudgetDto createBudgetDto, Long ownerId);

    boolean isBudgetValid(Long id);

    boolean isUserPermitted(User user, Long budgetId);
}
