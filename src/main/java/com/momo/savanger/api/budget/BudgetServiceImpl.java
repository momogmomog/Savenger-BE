package com.momo.savanger.api.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    @Override
    public Budget findBudgetById(long id) {
        return this.budgetRepository.findById(id).orElse(null);
    }
}
