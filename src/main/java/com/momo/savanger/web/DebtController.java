package com.momo.savanger.web;

import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.api.debt.DebtDto;
import com.momo.savanger.api.debt.DebtMapper;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class DebtController {

    private final DebtService debtService;

    private final DebtMapper debtMapper;

    @PostMapping(Endpoints.DEBTS)
    public DebtDto create(@Valid @RequestBody CreateDebtDto createDebtDto) {

        return this.debtMapper.toDebtDto(this.debtService.create(createDebtDto));
    }

}
