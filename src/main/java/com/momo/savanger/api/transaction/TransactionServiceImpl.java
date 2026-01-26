package com.momo.savanger.api.transaction;

import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;
import com.momo.savanger.api.transaction.dto.TransferTransactionPair;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.constants.EntityGraphs;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TagService tagService;

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
                recurringTransaction
        );
        transactionDto.setDateCreated(null);

        return this.create(transactionDto, null);
    }

    @Override
    @Transactional
    public void createDebtTransactions(Debt debt, BigDecimal amount) {

        final User user = SecurityUtils.getCurrentUser();

        this.create(
                this.createTransactionDtoForDebt(amount,
                        debt.getId(),
                        TransactionType.EXPENSE,
                        debt.getLenderBudgetId())
                , user.getId()
        );

        this.create(
                this.createTransactionDtoForDebt(amount,
                        debt.getId(),
                        TransactionType.INCOME,
                        debt.getReceiverBudgetId())
                , user.getId()
        );
    }

    @Override
    @Transactional
    public void payDebtTransaction(Debt debt, BigDecimal amount) {
        final Long userId = SecurityUtils.getCurrentUser().getId();

        this.create(
                this.createTransactionDtoForDebt(amount,
                        debt.getId(),
                        TransactionType.EXPENSE,
                        debt.getReceiverBudgetId())
                , userId
        );

        this.create(
                this.createTransactionDtoForDebt(amount,
                        debt.getId(),
                        TransactionType.INCOME,
                        debt.getLenderBudgetId())
                , userId
        );
    }

    private CreateTransactionServiceDto createTransactionDtoForDebt(BigDecimal amount, Long debtId,
            TransactionType transactionType, Long budgetId) {

        return CreateTransactionServiceDto.debtDto(amount,
                debtId,
                transactionType,
                budgetId
        );
    }

    private CreateTransactionServiceDto createTransactionDtoForTransfer(BigDecimal amount,
            TransactionType transactionType, Long budgetId, String comment, Long categoryId,
            Long transferTransactionId) {

        return CreateTransactionServiceDto.transferDto(transferTransactionId,
                amount,
                comment,
                categoryId,
                transactionType,
                budgetId
        );
    }

    @Override
    @Transactional
    public void createTransferTransactions(CreateTransferTransactionDto dto,
            Long transferTransactionId, Transfer transfer) {

        final Long userId = SecurityUtils.getCurrentUser().getId();

        this.create(
                this.createTransactionDtoForTransfer(
                        dto.getAmount(),
                        TransactionType.EXPENSE,
                        transfer.getSourceBudgetId(),
                        dto.getSourceComment(),
                        dto.getSourceCategoryId(),
                        transferTransactionId
                ),
                userId
        );

        this.create(
                this.createTransactionDtoForTransfer(
                        dto.getAmount(),
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
            Long transferTransactionId) {

        final TransferTransactionPair pair = new TransferTransactionPair();

        final List<Transaction> transactions = this.transactionRepository
                .findByTransferTransactionId(transferTransactionId);

        if (transactions.size() != 2) {
            log.warn(
                    "Found {} transactions for transfer transaction {} where exactly two are expected!",
                    transactions.size(),
                    transferTransactionId
            );
            throw ApiException.with(ApiErrorCode.ERR_0020);
        }

        pair.setSourceTransaction(transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .findFirst()
                .map(this.transactionMapper::toTransactionDto)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0020))
        );

        pair.setReceiverTransaction(transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .findFirst()
                .map(this.transactionMapper::toTransactionDto)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0020))
        );

        return pair;
    }

    @Override
    @Transactional
    public Transaction createCompensationTransaction(Long budgetId, BigDecimal amount) {

        return this.create(CreateTransactionServiceDto.compensateDto(amount, budgetId), null);

    }

    @Override
    public Page<Transaction> searchTransactions(TransactionSearchQuery query, User user) {
        final Specification<Transaction> specification = this.createCoreSearchSpecifications(query)
                .and(TransactionSpecifications.sort(query.getSort()));

        return this.transactionRepository.findAll(
                specification,
                query.getPage(),
                EntityGraphs.TRANSACTION_TAGS
        );
    }

    private Specification<Transaction> createCoreSearchSpecifications(
            TransactionSearchQuery query) {
        return TransactionSpecifications
                .budgetIdEquals(query.getBudgetId())
                .and(TransactionSpecifications.betweenAmount(query.getAmount()))
                .and(TransactionSpecifications.maybeContainsComment(query.getComment()))
                .and(TransactionSpecifications.betweenDate(query.getDateCreated()))
                .and(TransactionSpecifications.categoryIdContains(query.getCategoryIds()))
                .and(TransactionSpecifications.typeEquals(query.getType()))
                .and(TransactionSpecifications.maybeRevised(query.getRevised()))
                .and(TransactionSpecifications.userIdContains(query.getUserIds()))
                .and(TransactionSpecifications.debtIdEquals(query.getDebtId()))
                .and(TransactionSpecifications.noDebtTransactions(query.getNoDebtTransactions()))
                .and(TransactionSpecifications.tagsContain(query.getTagIds()));
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
    public void deleteTransferTransactions(Long transferTransactionId) {

        final TransferTransactionPair pair = this.getTransferTransactionPair(transferTransactionId);

        if (pair.getSourceTransaction().getRevised()
                || pair.getReceiverTransaction().getRevised()) {
            throw ApiException.with(ApiErrorCode.ERR_0021);
        }

        this.transactionRepository.deleteByTransferTransactionId(transferTransactionId);
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
                .and(TransactionSpecifications.maybeRevised(false))
                .and(TransactionSpecifications.noDebtTransactions(true))
                .and(TransactionSpecifications.noPrepaymentTransaction())
                .and(TransactionSpecifications.noTransferTransaction());

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

    @Override
    public List<Long> extractCategoryIds(TransactionSearchQuery query) {
        query.setNoDebtTransactions(true);
        return this.transactionRepository.getCategoryIds(
                this.createCoreSearchSpecifications(query));
    }

    @Override
    public List<Long> extractTagIds(TransactionSearchQuery query) {
        query.setNoDebtTransactions(true);
        return this.transactionRepository.getTagIds(this.createCoreSearchSpecifications(query));
    }

    @Override
    public TransactionSumAndCount sumAndCount(TransactionSearchQuery query) {
        query.setNoDebtTransactions(true);
        final Specification<Transaction> specification = this.createCoreSearchSpecifications(query);

        return this.transactionRepository.sumAndCount(specification);
    }
}
