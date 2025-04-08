package com.momo.savanger.api.budget;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final BudgetMapper budgetMapper;

    @Override
    public Budget findBudgetById(Long id) {
        return this.budgetRepository.findById(id).orElse(null);
    }

    @Override
    public Budget saveBudget(CreateBudgetDto createBudgetDto, Long ownerId) {

        Budget budget = this.budgetMapper.toBudget(createBudgetDto);

        budget.setOwnerId(ownerId);

        if (budget.getBudgetCap() == null) {
            budget.setBudgetCap(BigDecimal.ZERO);
        }
        if (budget.getBalance() == null) {
            budget.setBalance(BigDecimal.ZERO);
        }

        try {
            this.budgetRepository.saveAndFlush(budget);
        } catch (Exception e) {
            return null;
        }

        return budget;
    }

    @Override
    public List<Budget> findAllBudgets() {
        return this.budgetRepository.findAll();
    }
}
