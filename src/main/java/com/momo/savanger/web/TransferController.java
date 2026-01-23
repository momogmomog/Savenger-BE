package com.momo.savanger.web;

import com.momo.savanger.api.transfer.TransferMapper;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.constraints.CanAccessTransfer;
import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferFullDto;
import com.momo.savanger.api.transfer.dto.TransferSearchQuery;
import com.momo.savanger.api.transfer.dto.TransferSimpleDto;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionService;
import com.momo.savanger.api.transfer.transferTransaction.constraints.CanAccessTransferTransaction;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class TransferController {

    private final TransferService transferService;

    private final TransferMapper transferMapper;

    private final TransferTransactionService transferTransactionService;


    @PutMapping(Endpoints.TRANSFERS)
    public TransferFullDto createTransfer(@Valid @RequestBody CreateTransferDto transferDto) {

        return this.transferMapper.toTransferFullDto(this.transferService.upsert(transferDto));
    }

    @DeleteMapping(Endpoints.TRANSFER)
    public void deleteTransfer(@PathVariable("id") @CanAccessTransfer Long transferId) {

        this.transferService.disable(transferId);
    }

    @PostMapping(Endpoints.TRANSFERS_SEARCH)
    public PagedModel<TransferSimpleDto> searchTransfer(
            @Valid @RequestBody TransferSearchQuery query) {

        final PagedModel<TransferSimpleDto> pagedModel = new PagedModel<>(this.transferService
                .searchTransfers(query)
                .map(this.transferMapper::toTransferSimpleDto));

        return pagedModel;
    }

    @PostMapping(Endpoints.TRANSFER_TRANSACTIONS)
    public TransferTransactionDto createTransferTransaction(
            @Valid @RequestBody CreateTransferTransactionDto transferTransactionDto) {

        return this.transferTransactionService.create(transferTransactionDto);
    }

    @GetMapping(Endpoints.TRANSFER_TRANSACTION)
    public TransferTransactionDto getTransferTransaction(
            @PathVariable("id") @CanAccessTransferTransaction Long transferTransactionId) {

        return this.transferTransactionService.getTransferTransactionDto(transferTransactionId);
    }

    @DeleteMapping(Endpoints.TRANSFER_TRANSACTION)
    public void deleteTransferTransaction(
            @PathVariable("id") @CanAccessTransferTransaction Long transferTransactionId) {

        this.transferTransactionService.revertTransferTransaction(transferTransactionId);
    }
}
