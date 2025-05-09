package com.momo.savanger.web;

import com.momo.savanger.api.transaction.CreateTransactionDto;
import com.momo.savanger.api.transaction.TransactionDto;
import com.momo.savanger.api.transaction.TransactionMapper;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
                this.transactionService.create(transactionDto, user));
    }

}
