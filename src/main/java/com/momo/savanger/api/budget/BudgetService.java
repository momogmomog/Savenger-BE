package com.momo.savanger.api.budget;

import java.util.List;

public interface BudgetService {

    Budget findById(Long id);

    Budget create(CreateBudgetDto createBudgetDto, Long ownerId);

    List<Budget> findAll();
}
