package com.momo.savanger.api.debt;

import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
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

    @Override
    public Debt findById(Long id) {
        return this.debtRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0013));
    }

    @Override
    @Transactional
    public Debt create(CreateDebtDto dto) {

        final Optional<Debt> maybeDebt = this.findDebt(dto.getReceiverBudgetId(),
                dto.getLenderBudgetId());
        final Debt debt = maybeDebt.orElse(new Debt());

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
        Debt debt = this.findById(id);

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


    @Override
    public BigDecimal getSumByLenderBudgetId(Long budgetId) {
        return this.ifSumIsNullReturnZero(
                this.debtRepository.sumDebtByLenderBudgetId(budgetId)
        );
    }

    @Override
    public BigDecimal getSumByReceiverBudgetId(Long receiverBudgetId) {
        return this.ifSumIsNullReturnZero(
                this.debtRepository.sumDebtByReceiverBudgetId(receiverBudgetId)
        );
    }

    private BigDecimal ifSumIsNullReturnZero(BigDecimal sum) {
        if (sum == null) {
            return BigDecimal.ZERO;
        }

        return sum;
    }
}
