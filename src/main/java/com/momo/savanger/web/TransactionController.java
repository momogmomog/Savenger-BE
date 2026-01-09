package com.momo.savanger.web;

import com.momo.savanger.api.transaction.TransactionMapper;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.constraints.CanDeleteTransaction;
import com.momo.savanger.api.transaction.constraints.CanViewTransaction;
import com.momo.savanger.api.transaction.constraints.TransactionNotRevised;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class TransactionController {

    private final TransactionService transactionService;

    private final TransactionMapper transactionMapper;

    @PostMapping(value = Endpoints.TRANSACTIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    public TransactionDto create(@Valid @RequestBody CreateTransactionDto transactionDto,
            @AuthenticationPrincipal User user) {

        return this.transactionMapper.toTransactionDto(
                this.transactionService.create(
                        this.transactionMapper.toCreateServiceDto(transactionDto),
                        user.getId()
                )
        );
    }

    @PostMapping(Endpoints.TRANSACTIONS_SEARCH)
    public PagedModel<TransactionDto> searchTransactions(
            @Valid @RequestBody TransactionSearchQuery query,
            @AuthenticationPrincipal User user) {
        return new PagedModel<>(this.transactionService
                .searchTransactions(query, user)
                .map(this.transactionMapper::toTransactionDto)
        );
    }

    @PutMapping(Endpoints.TRANSACTION)
    public TransactionDto edit(@PathVariable @TransactionNotRevised Long id,
            @Valid @RequestBody EditTransactionDto dto) {

        return this.transactionMapper.toTransactionDto(
                this.transactionService.edit(id, dto));
    }

    @DeleteMapping(Endpoints.TRANSACTION)
    public void delete(@PathVariable @CanDeleteTransaction Long id) {

        this.transactionService.deleteById(id);

    }

    @GetMapping(Endpoints.TRANSACTION)
    public TransactionDto getTransaction(@PathVariable @CanViewTransaction Long id) {

        return this.transactionMapper.toTransactionDto(this.transactionService.findById(id));
    }
}
