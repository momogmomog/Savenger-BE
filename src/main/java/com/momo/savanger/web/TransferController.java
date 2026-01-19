package com.momo.savanger.web;

import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferDto;
import com.momo.savanger.api.transfer.TransferMapper;
import com.momo.savanger.api.transfer.dto.TransferSearchQuery;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.constraints.CanAccessTransfer;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransaction;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionService;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class TransferController {

    private final TransferService transferService;

    private final TransferMapper transferMapper;

    private final TransferTransactionService transferTransactionService;


    @PutMapping(Endpoints.TRANSFERS)
    public TransferDto transfer(@Valid @RequestBody CreateTransferDto transferDto) {

        return this.transferMapper.toTransferDto(this.transferService.upsert(transferDto));
    }

    @DeleteMapping(Endpoints.TRANSFER)
    public void transfer(@PathVariable("id") @CanAccessTransfer Long transferId) {

        this.transferService.disable(transferId);
    }

    @PostMapping(Endpoints.TRANSFERS_SEARCH)
    public PagedModel<TransferDto> searchTransfer(
            @Valid @RequestBody TransferSearchQuery query) {

        final PagedModel<TransferDto> pagedModel = new PagedModel<>(this.transferService
                .searchTransfer(query)
                .map(this.transferMapper::toTransferDto));

        return pagedModel;
    }

    @PostMapping(Endpoints.TRANSFER_TRANSACTION)
    public TransferTransactionDto transferTransaction(
            @Valid @RequestBody CreateTransferTransactionDto transferTransactionDto) {

        final TransferTransaction transferTransaction = this.transferTransactionService.create(
                transferTransactionDto);

        return this.transferTransactionService.getTransferTransactionDto(
                transferTransactionDto.getTransferId(), transferTransaction.getId());
    }
}
