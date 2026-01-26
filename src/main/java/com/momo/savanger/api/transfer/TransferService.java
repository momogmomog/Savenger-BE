package com.momo.savanger.api.transfer;


import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferSearchQuery;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface TransferService {

    Transfer getById(Long id);

    Transfer findAndFetchDetails(Long id);

    Transfer upsert(CreateTransferDto createTransferDto);

    void disable(Long id);

    Optional<Transfer> findTransfer(Long receiverBudgetId, Long sourceBudgetId);

    Page<Transfer> searchTransfers(TransferSearchQuery query);
}
