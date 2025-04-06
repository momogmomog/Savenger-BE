package com.momo.savanger.api.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final BudgetMapper budgetMapper;

    @Override
    public Budget findBudgetById(long id) {
        return this.budgetRepository.findById(id).orElse(null);
    }

    @Override
    public Budget saveBudget(BudgetDto budgetDto) {

        Budget budget = this.budgetMapper.toBudget(budgetDto);

        //Set current owner ID
        budget.setOwnerId(1L);

        this.budgetRepository.saveAndFlush(budget);

        return budget;
    }
}
