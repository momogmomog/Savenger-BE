package com.momo.savanger.api.transaction;

import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.transaction.dto.TransferTransactionPair;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransaction;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TagService tagService;

    private final TransferService transferService;

    @Override
    public Transaction findById(Long id) {
        return this.transactionRepository
                .findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0010));
    }

    @Override
    public Transaction findAndFetchDetails(Long id) {
        return this.transactionRepository
                .findTransactionById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0010));
    }

    @Override
    @Transactional
    public Transaction create(CreateTransactionServiceDto dto, Long userId) {
        final Transaction transaction = this.transactionMapper.toTransaction(dto);

        if (transaction.getDateCreated() == null) {
            transaction.setDateCreated(LocalDateTime.now());
        }

        transaction.setUserId(userId);
        transaction.setRevised(false);

        if (!dto.getTagIds().isEmpty()) {
            transaction.setTags(this.tagService.findByBudgetAndIdContaining(
                    dto.getTagIds(),
                    dto.getBudgetId()
            ));
        }

        this.transactionRepository.saveAndFlush(transaction);

        return this.findById(transaction.getId());
    }

    @Override
    @Transactional
    public Transaction createPrepaymentTransaction(RecurringTransaction recurringTransaction) {
        final CreateTransactionServiceDto transactionDto = this.transactionMapper.toCreateServiceDto(
                recurringTransaction);
        transactionDto.setDateCreated(null);

        return this.create(transactionDto, null);
    }

    @Override
    @Transactional
    public void createDebtTransactions(Debt debt, BigDecimal amount) {

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.EXPENSE,
                        debt.getLenderBudgetId(), null, null, null)
                , null
        );

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.INCOME,
                        debt.getReceiverBudgetId(), null, null, null)
                , null
        );
    }

    @Override
    @Transactional
    public void payDebtTransaction(Debt debt, BigDecimal amount) {
        final Long userId = SecurityUtils.getCurrentUser().getId();

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.EXPENSE,
                        debt.getReceiverBudgetId(), null, null, null)
                , userId
        );

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.INCOME,
                        debt.getLenderBudgetId(), null, null, null)
                , userId
        );
    }

    private CreateTransactionServiceDto createTransactionDto(BigDecimal amount, Long debtId,
            TransactionType transactionType, Long budgetId, String comment, Long categoryId,
            Long transferTransactionId) {

       final CreateTransactionServiceDto dto;

        if (debtId != null) {
            dto = CreateTransactionServiceDto.debtDto(amount,
                    debtId,
                    transactionType,
                    budgetId
            );
        } else if (transferTransactionId != null) {
            dto = CreateTransactionServiceDto.transferDto(transferTransactionId,
                    amount,
                    comment,
                    categoryId,
                    transactionType,
                    budgetId
            );
        } else {
            throw ApiException.with(ApiErrorCode.ERR_0020);
        }

        return dto;
    }

    @Override
    @Transactional
    public void createTransferTransactions(CreateTransferTransactionDto dto,
            Long transferTransactionId, Transfer transfer) {

        final Long userId = SecurityUtils.getCurrentUser().getId();

        this.create(
                this.createTransactionDto(
                        dto.getAmount(),
                        null,
                        TransactionType.EXPENSE,
                        transfer.getSourceBudgetId(),
                        dto.getSourceComment(),
                        dto.getSourceCategoryId(),
                        transferTransactionId
                ),
                userId
        );

        this.create(
                this.createTransactionDto(dto.getAmount(),
                        null,
                        TransactionType.INCOME,
                        transfer.getReceiverBudgetId(),
                        dto.getReceiverComment(),
                        dto.getReceiverCategoryId(),
                        transferTransactionId
                ),
                userId
        );
    }

    @Override
    public TransferTransactionPair getTransferTransactionPair(
            TransferTransaction transferTransaction) {

        final TransferTransactionPair transferTransactionPair = new TransferTransactionPair();

        final Transfer transfer = this.transferService.getById(transferTransaction.getTransferId());

       final TransactionDto sourceTransaction = this.transactionMapper.toTransactionDto(
                this.transactionRepository
                        .getByTransferTransactionIdAndBudgetId(transferTransaction.getId(),
                                transfer.getSourceBudgetId())
        );

        final TransactionDto receiverTransaction = this.transactionMapper.toTransactionDto(
                this.transactionRepository
                        .getByTransferTransactionIdAndBudgetId(transferTransaction.getId(),
                                transfer.getReceiverBudgetId())
        );

        transferTransactionPair.setSourceTransaction(sourceTransaction);
        transferTransactionPair.setReceiverTransaction(receiverTransaction);

        return transferTransactionPair;
    }

    @Override
    @Transactional
    public Transaction createCompensationTransaction(Long budgetId, BigDecimal amount) {

        return this.create(CreateTransactionServiceDto.compensateDto(amount, budgetId), null);

    }


    @Override
    public Page<Transaction> searchTransactions(TransactionSearchQuery query, User user) {
        final Specification<Transaction> specification = TransactionSpecifications
                .budgetIdEquals(query.getBudgetId())
                .and(TransactionSpecifications.sort(query.getSort()))
                .and(TransactionSpecifications.betweenAmount(query.getAmount()))
                .and(TransactionSpecifications.maybeRevised(query.getRevised()))
                .and(TransactionSpecifications.maybeContainsComment(query.getComment()))
                .and(TransactionSpecifications.betweenDate(query.getDateCreated()))
                .and(TransactionSpecifications.categoryIdContains(query.getCategoryIds()))
                .and(TransactionSpecifications.typeEquals(query.getType()))
                .and(TransactionSpecifications.userIdContains(query.getUserIds()))
                .and(TransactionSpecifications.tagsContain(query.getTagIds()));

        return this.transactionRepository.findAll(specification, query.getPage(), null);
    }

    @Override
    @Transactional
    public Transaction edit(Long id, EditTransactionDto dto) {

        final Transaction transaction = this.transactionMapper.mergeIntoTransaction(
                dto,
                this.findById(id)
        );

        if (!dto.getTagIds().isEmpty()) {
            transaction.setTags(this.tagService.findByBudgetAndIdContaining(
                    dto.getTagIds(),
                    dto.getBudgetId()
            ));
        }

        this.transactionRepository.saveAndFlush(transaction);

        return this.findAndFetchDetails(id);
    }

    @Override
    public Boolean existsByIdAndRevisedFalse(Long id) {
        return this.transactionRepository.existsByIdAndRevisedFalse(id);
    }

    @Override
    public void deleteById(Long id) {

        this.transactionRepository.deleteById(id);

    }

    @Override
    public boolean canDeleteTransaction(Long transactionId, User user) {
        final Specification<Transaction> specification = TransactionSpecifications
                .idEquals(transactionId)
                .and(TransactionSpecifications.userIdEquals(user.getId()))
                .and(TransactionSpecifications.maybeRevised(false));

        return this.transactionRepository.exists(specification);
    }

    @Override
    public boolean canViewTransaction(Long transactionId, Long userId) {

        return this.transactionRepository.existOwnerOrParticipant(transactionId, userId);
    }

    @Override
    public void reviseTransactions(Long budgetId) {
        this.transactionRepository.setRevisedTrue(budgetId);
    }

    @Override
    public BigDecimal getExpensesAmount(Long budgetId) {

        return this.getSumAmount(budgetId, TransactionType.EXPENSE);
    }

    @Override
    public BigDecimal getEarningsAmount(Long budgetId) {

        return this.getSumAmount(budgetId, TransactionType.INCOME);
    }


    @Override
    public BigDecimal getDebtLendedAmount(Long budgetId) {
        return Objects.requireNonNullElse(
                this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(budgetId,
                        TransactionType.EXPENSE), BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getDebtReceivedAmount(Long budgetId) {
        return Objects.requireNonNullElse(
                this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(budgetId,
                        TransactionType.INCOME), BigDecimal.ZERO);
    }

    private BigDecimal getSumAmount(Long budgetId, TransactionType type) {
        return Objects.requireNonNullElse(
                this.transactionRepository.sumAmountByBudgetIdAndTypeOfNonRevisedNonDebt(
                        budgetId,
                        type
                ),
                BigDecimal.ZERO
        );
    }

    @Override
    public BigDecimal getPrepaymentPaidAmount(Long prepaymentId) {

        final BigDecimal amount = this.transactionRepository.sumByPrepaymentIdAndType(
                TransactionType.EXPENSE,
                prepaymentId
        );

        if (amount == null) {
            return BigDecimal.ZERO;
        }

        return amount;
    }
}
