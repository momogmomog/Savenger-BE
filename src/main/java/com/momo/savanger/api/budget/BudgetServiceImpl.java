package com.momo.savanger.api.budget;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
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
    public Budget findById(Long id) {
        return this.budgetRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0004));
    }

    @Override
    public Budget create(CreateBudgetDto createBudgetDto, Long ownerId) {

        final Budget budget = this.budgetMapper.toBudget(createBudgetDto);

        budget.setOwnerId(ownerId);

        if (budget.getBudgetCap() == null) {
            budget.setBudgetCap(BigDecimal.ZERO);
        }
        if (budget.getBalance() == null) {
            budget.setBalance(BigDecimal.ZERO);
        }

        this.budgetRepository.saveAndFlush(budget);

        return this.findById(budget.getId());
    }

    @Override
    public List<Budget> findAll() {
        return this.budgetRepository.findAll();
    }
}
