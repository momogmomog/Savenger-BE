package com.momo.savanger.api.debt;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;

    private final DebtMapper debtMapper;

    private final TransactionService transactionService;

    private final BudgetService budgetService;

    @Override
    public Debt findById(Long id) {
        return this.debtRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0013));
    }

    @Override
    @Transactional
    public Debt create(CreateDebtDto dto) {

        final BudgetStatistics lender = this.budgetService.getStatistics(dto.getLenderBudgetId());

        if (dto.getDebtAmount().compareTo(lender.getRealBalance()) > 0) {
            throw ApiException.with(ApiErrorCode.ERR_0014);
        }

        final Debt debt = this.findDebt(dto.getReceiverBudgetId(),
                dto.getLenderBudgetId()
        ).orElse(new Debt());

        this.debtMapper.mergeIntoDebt(dto, debt);

        if (debt.getAmount() == null) {
            debt.setAmount(BigDecimal.ZERO);
        }

        debt.setAmount(debt.getAmount().add(dto.getDebtAmount()));

        this.debtRepository.save(debt);

        this.transactionService.createDebtTransactions(debt, dto.getDebtAmount());

        return this.findById(debt.getId());
    }

    @Override
    public Debt pay(Long id, PayDebtDto dto) {

        final Debt debt = this.findById(id);

        final BudgetStatistics budget = budgetService.getStatistics(debt.getReceiverBudgetId());

        final User user = SecurityUtils.getCurrentUser();

        if (!Objects.equals(budget.getBudget().getOwnerId(), user.getId())) {
            throw ApiException.with(ApiErrorCode.ERR_0015);
        }

        if (debt.getAmount().compareTo(dto.getAmount()) < 0) {
            dto.setAmount(debt.getAmount());
        }

        if (budget.getRealBalance().compareTo(dto.getAmount()) < 0) {
            throw ApiException.with(ApiErrorCode.ERR_0014);
        }

        debt.setAmount(debt.getAmount().subtract(dto.getAmount()));

        this.debtRepository.save(debt);

        this.transactionService.payDebtTransaction(debt, dto.getAmount());

        return debt;
    }

    @Override
    public Optional<Debt> findDebt(Long receiverBudgetId, Long lenderBudgetId) {
        final Specification<Debt> specification = DebtSpecifications.lenderBudgetIdEquals(
                        lenderBudgetId)
                .and(DebtSpecifications.receiverBudgetIdEquals(receiverBudgetId));

        return this.debtRepository.findAll(specification, null).stream().findFirst();
    }


}
