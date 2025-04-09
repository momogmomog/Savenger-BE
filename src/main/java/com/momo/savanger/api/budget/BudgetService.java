package com.momo.savanger.api.budget;

public interface BudgetService {

    Budget findById(Long id);

    Budget create(CreateBudgetDto createBudgetDto, Long ownerId);
}
