package com.momo.savanger.api.budget;

import static com.momo.savanger.api.budget.BudgetSpecifications.ownerIdEquals;

import com.momo.savanger.api.user.User;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    public boolean isBudgetValid(Long id) {
        final Specification<Budget> specification = BudgetSpecifications.idEquals(id)
                .and(BudgetSpecifications.isActive());

        return this.budgetRepository.exists(specification);
    }

    @Override
    public boolean isUserPermitted(User user, Long budgetId) {

        final Specification<Budget> specification = BudgetSpecifications.idEquals(budgetId)
                .and(BudgetSpecifications.isActive())
                .and(
                        ownerIdEquals(user.getId())
                                .or(BudgetSpecifications.containsParticipant(user.getId()))
                );

        return this.budgetRepository.exists(specification);
    }
}
