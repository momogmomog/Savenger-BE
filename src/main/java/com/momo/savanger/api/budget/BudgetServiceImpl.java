package com.momo.savanger.api.budget;

import static com.momo.savanger.api.budget.BudgetSpecifications.ownerIdEquals;

import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.budget.dto.BudgetSearchQuery;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UnassignParticipantDto;
import com.momo.savanger.api.budget.dto.UpdateBudgetDto;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.recurringRule.RecurringRuleService;
import com.momo.savanger.api.revision.Revision;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.constants.EntityGraphs;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final BudgetMapper budgetMapper;

    private final UserService userService;

    private final TransactionService transactionService;

    private final PrepaymentService prepaymentService;

    private final RecurringRuleService recurringRuleService;

    @Override
    public Budget findById(Long id) {
        return this.budgetRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0004));
    }

    @Override
    public Budget findByIdFetchAll(Long id) {
        return this.budgetRepository.findAll(BudgetSpecifications.idEquals(id),
                        EntityGraphs.BUDGET_ALL)
                .stream()
                .findFirst()
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0004));
    }

    @Override
    public Optional<Budget> findIfValid(Long id) {
        final Specification<Budget> specification = BudgetSpecifications.idEquals(id)
                .and(BudgetSpecifications.isActive());

        final List<Budget> budgets = this.budgetRepository.findAll(
                specification,
                EntityGraphs.BUDGET_ALL
        );
        return budgets.stream().findFirst();
    }

    @Override
    public Budget create(CreateBudgetDto createBudgetDto, Long ownerId) {

        final Budget budget = this.budgetMapper.toBudget(createBudgetDto);

        budget.setOwnerId(ownerId);

        budget.setDueDate(
                this.recurringRuleService.convertRecurringRuleToDate(
                        createBudgetDto.getRecurringRule(),
                        createBudgetDto.getDateStarted())
        );

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
    @Transactional
    public Budget update(UpdateBudgetDto updateBudgetDto, Long budgetId) {
        final Budget budget = this.findById(budgetId);

        this.budgetMapper.mergeIntoBudget(updateBudgetDto, budget);

        this.budgetRepository.save(budget);

        return this.findById(budgetId);
    }

    @Override
    public boolean isBudgetValid(Long id) {
        final Specification<Budget> specification = BudgetSpecifications.idEquals(id)
                .and(BudgetSpecifications.isActive());

        return this.budgetRepository.exists(specification);
    }

    @Override
    public boolean isUserPermitted(User user, Long budgetId) {
        return this.isUserPermitted(user, budgetId, true);
    }

    @Override
    public boolean isUserPermitted(User user, Long budgetId, boolean filterOnlyActiveBudgets) {
        final Specification<Budget> specification = BudgetSpecifications.idEquals(budgetId)
                .and(filterOnlyActiveBudgets ? BudgetSpecifications.isActive()
                        : Specification.where(null))
                .and(
                        ownerIdEquals(user.getId())
                                .or(BudgetSpecifications.containsParticipant(user.getId()))
                );
        return this.budgetRepository.exists(specification);
    }

    @Override
    @Transactional
    public void addParticipant(AssignParticipantDto dto) {
        final Budget budget = this.findById(dto.getBudgetRef().getId());

        final User participant = this.userService.getById(dto.getParticipantId());

        budget.getParticipants().add(participant);

        this.budgetRepository.save(budget);
    }

    @Override
    @Transactional
    public void deleteParticipant(UnassignParticipantDto dto) {
        final Budget budget = this.findById(dto.getBudgetRef().getId());

        final User participant = this.userService.getById(dto.getParticipantId());

        budget.getParticipants().remove(participant);

        this.budgetRepository.save(budget);
    }

    @Override
    public Page<Budget> searchBudget(BudgetSearchQuery query, User user) {
        final Specification<Budget> specification = Specification.anyOf(
                        BudgetSpecifications.ownerIdEquals(user.getId()),
                        BudgetSpecifications.containsParticipant(user.getId())
                )
                .and(BudgetSpecifications.sort(query.getSort()))
                .and(BudgetSpecifications.isActive(query.getActive()))
                .and(BudgetSpecifications.budgetNameContains(query.getBudgetName()))
                .and(BudgetSpecifications.betweenDateStarted(query.getDateStarted()))
                .and(BudgetSpecifications.betweenDueDate(query.getDueDate()))
                .and(BudgetSpecifications.betweenBalance(query.getBalance()))
                .and(BudgetSpecifications.betweenBudgetCap(query.getBudgetCap()))
                .and(BudgetSpecifications.isAutoRevise(query.getAutoRevise()));

        return this.budgetRepository.findAll(specification, query.getPage(), null);
    }

    @Override
    @Transactional
    public void updateBudgetAfterRevision(Long id, Revision revision) {
        final Budget budget = this.findById(id);

        budget.setBalance(revision.getBalance());
        budget.setDateStarted(revision.getRevisionDate());
        budget.setDueDate(this.recurringRuleService.convertRecurringRuleToDate(
                budget.getRecurringRule(), budget.getDateStarted())
        );

        this.budgetRepository.save(budget);
    }

    @Override
    public BudgetStatistics getStatistics(Long budgetId) {
        final Budget budget = this.findById(budgetId);
        return this.getStatistics(budget);
    }

    @Override
    public BudgetStatistics getStatisticsFetchAll(Long budgetId) {
        final Budget budget = this.findByIdFetchAll(budgetId);
        return this.getStatistics(budget);
    }

    private BudgetStatistics getStatistics(Budget budget) {
        final Long budgetId = budget.getId();
        final BudgetStatistics statisticDto = new BudgetStatistics();

        final BigDecimal earnings = this.transactionService.getEarningsAmount(budgetId);
        final BigDecimal expenses = this.transactionService.getExpensesAmount(budgetId);

        final BigDecimal debtLendedSum = this.transactionService.getDebtLendedAmount(budgetId);
        final BigDecimal debtReceivedSum = this.transactionService.getDebtReceivedAmount(budgetId);

        final BigDecimal prepaymentsAmount = this.prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(
                budgetId);

        statisticDto.setBudget(budget);

        statisticDto.setEarningsAmount(earnings);
        statisticDto.setExpensesAmount(expenses);
        statisticDto.setDebtLendedAmount(debtLendedSum);
        statisticDto.setDebtReceivedAmount(debtReceivedSum);

        statisticDto.setRealBalance(this.sumRealBalance(
                budget,
                earnings,
                expenses,
                prepaymentsAmount)
        );

        statisticDto.setBalance(
                statisticDto.getRealBalance()
                        .subtract(statisticDto.getDebtReceivedAmount())
                        .add(statisticDto.getDebtLendedAmount())
                        .add(prepaymentsAmount)
        );

        return statisticDto;
    }

    private BigDecimal sumRealBalance(
            Budget budget,
            BigDecimal earningsAmount,
            BigDecimal expensesAmount,
            BigDecimal prepaymentAmount) {

        return earningsAmount
                .add(budget.getBalance())
                .subtract(expensesAmount)
                .subtract(prepaymentAmount);
    }
}
