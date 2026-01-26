package com.momo.savanger.api.transfer;

import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferSearchQuery;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.constants.EntityGraphs;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;

    private final TransferMapper transferMapper;

    @Override
    public Transfer getById(Long id) {
        return this.transferRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0018)
                );
    }

    @Override
    public Transfer findAndFetchDetails(Long id) {
        return this.transferRepository
                .findTransferById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0018)
                );
    }

    @Override
    @Transactional
    public Transfer upsert(CreateTransferDto dto) {
        final Transfer transfer = this.findTransfer(
                        dto.getReceiverBudgetId(),
                        dto.getSourceBudgetId()
                )
                .orElseGet(() -> this.transferMapper.toTransfer(dto));

        transfer.setActive(true);

        this.transferRepository.saveAndFlush(transfer);

        return this.getById(transfer.getId());
    }

    @Override
    @Transactional
    public void disable(Long id) {

        final Transfer transfer = this.getById(id);

        transfer.setActive(false);

        this.transferRepository.save(transfer);
    }

    @Override
    public Optional<Transfer> findTransfer(Long receiverBudgetId, Long sourceBudgetId) {
        final Specification<Transfer> specification = TransferSpecifications
                .sourceBudgetIdEquals(sourceBudgetId)
                .and(TransferSpecifications.receiverBudgetIdEquals(receiverBudgetId));

        return this.transferRepository.findAll(specification, null).stream().findFirst();
    }

    @Override
    public Page<Transfer> searchTransfers(TransferSearchQuery query) {

        final Long userId = SecurityUtils.getCurrentUser().getId();
        final Specification<Transfer> specification = TransferSpecifications
                .sourceBudgetIdEquals(query.getSourceBudgetId())
                .and(TransferSpecifications.receivedBudgetIdIn(query.getReceiverBudgetIds()))
                .and(TransferSpecifications.isActive(query.getActive()))
                .and(TransferSpecifications.canAccessReceiverBudget(userId));

        final Page<Transfer> transfers = this.transferRepository.findAll(specification,
                query.getPage(),
                EntityGraphs.TRANSFER_RECEIVER_BUDGET
        );

        return transfers;
    }
}
