package com.momo.savanger.api.transfer;


import java.util.Optional;
import org.springframework.data.domain.Page;

public interface TransferService {

    Transfer getById(Long id);

    Optional<Transfer> findById(Long id);

    Transfer upsert(CreateTransferDto createTransferDto);

    void disable(Long id);

    Optional<Transfer> findTransfer(Long receiverBudgetId, Long sourceBudgetId);

    Page<Transfer> searchTransfer(TransferSearchQuery query);
}
