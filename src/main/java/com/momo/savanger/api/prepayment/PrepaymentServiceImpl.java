package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.recurringRule.RecurringRuleService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrepaymentServiceImpl implements PrepaymentService {

    private final PrepaymentRepository prepaymentRepository;

    private final PrepaymentMapper prepaymentMapper;

    private final RecurringTransactionService recurringTransactionService;

    private final RecurringRuleService recurringRuleService;

    private final TransactionService transactionService;

    @Override
    public Prepayment findById(Long id) {
        return this.prepaymentRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0015)
                );
    }

    @Override
    public Optional<Prepayment> findByIdIfExists(Long id) {
        return this.findByIdIfExists(id, false);
    }

    @Override
    public Optional<Prepayment> findByIdIfExists(Long id, boolean allowCompleted) {
        final Specification<Prepayment> specification = PrepaymentSpecifications
                .idEquals(id)
                .and(allowCompleted ? Specification.where(null)
                        : PrepaymentSpecifications.isCompleted(false));

        final List<Prepayment> prepayments = this.prepaymentRepository.findAll(
                specification, null
        );
        return prepayments.stream().findFirst();
    }

    @Override
    @Transactional
    public Prepayment create(CreatePrepaymentDto dto) {
        final Prepayment prepayment = this.prepaymentMapper.toPrepayment(dto);

        prepayment.setCompleted(false);
        prepayment.setRemainingAmount(
                dto.getAmount()
        );

        return this.upsert(prepayment, dto);
    }

    @Override
    @Transactional
    public Prepayment edit(Long id, CreatePrepaymentDto dto) {
        final Prepayment existingPrepayment = this.findById(id);

        if (!existingPrepayment.getBudgetId().equals(dto.getBudgetId())) {
            throw ApiException.with(ApiErrorCode.ERR_0024);
        }

        this.prepaymentMapper.mergeIntoPrepayment(dto, existingPrepayment);

        final BigDecimal paidAmount = this.transactionService.getPrepaymentPaidAmount(id);
        final BigDecimal remainingAmount = existingPrepayment.getAmount().subtract(paidAmount);

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            existingPrepayment.setRemainingAmount(BigDecimal.ZERO);
            existingPrepayment.setCompleted(true);
        } else {
            existingPrepayment.setRemainingAmount(remainingAmount);
            existingPrepayment.setCompleted(false);
        }

        return this.upsert(existingPrepayment, dto);
    }

    private Prepayment upsert(Prepayment prepayment, CreatePrepaymentDto dto) {
        this.prepaymentRepository.saveAndFlush(prepayment);

        final RecurringTransaction rTransaction;

        if (dto.getRecurringTransaction() != null) {
            dto.getRecurringTransaction().setType(TransactionType.EXPENSE);

            rTransaction = this.recurringTransactionService.create(
                    dto.getRecurringTransaction()
            );
        } else if (dto.getRecurringTransactionId() != null) {
            rTransaction = this.recurringTransactionService.findById(
                    dto.getRecurringTransactionId()
            );
        } else {
            rTransaction = null;
        }

        if (rTransaction != null) {
            rTransaction.setPrepaymentId(prepayment.getId());
            if (Boolean.TRUE.equals(prepayment.getCompleted())) {
                rTransaction.setCompleted(true);
            }
            this.recurringTransactionService.updateRecurringTransaction(rTransaction);
        }

        return this.findById(
                prepayment.getId()
        );
    }

    @Override
    public BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long budgetId) {
        final BigDecimal sum = this.prepaymentRepository.sumPrepaymentAmountByBudgetId(budgetId);

        if (sum == null) {
            return BigDecimal.ZERO;
        }
        return sum;
    }

    @Override
    public void updatePrepaymentAfterPay(Prepayment prepayment) {

        prepayment.setRemainingAmount(
                prepayment.getAmount().subtract(
                        this.transactionService.getPrepaymentPaidAmount(
                                prepayment.getId()
                        )
                )
        );

        if (prepayment.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            prepayment.setCompleted(true);
        }

        prepayment.setUpdateDate(LocalDateTime.now());

        this.prepaymentRepository.save(prepayment);
    }

    @Override
    public Page<Prepayment> search(PrepaymentQuery query) {
        final Specification<Prepayment> spec = PrepaymentSpecifications
                .sort(query.getSort())
                .and(PrepaymentSpecifications.budgetIdEquals(query.getBudgetId()))
                .and(PrepaymentSpecifications.betweenAmount(query.getAmount()))
                .and(PrepaymentSpecifications.nameEquals(query.getName()))
                .and(PrepaymentSpecifications.betweenPaidUntil(query.getPaidUntil()))
                .and(PrepaymentSpecifications.isCompleted(query.getCompleted()))
                .and(PrepaymentSpecifications.betweenRemainingAmount(query.getRemainingAmount()));

        return this.prepaymentRepository.findAll(
                spec,
                query.getPage(),
                null
        );
    }
}
