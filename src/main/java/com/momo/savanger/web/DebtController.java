package com.momo.savanger.web;

import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.api.debt.DebtDto;
import com.momo.savanger.api.debt.DebtMapper;
import com.momo.savanger.api.debt.DebtQuery;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.api.debt.PayDebtDto;
import com.momo.savanger.api.debt.constraints.CanAccessDebt;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
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

    @GetMapping(Endpoints.DEBT)
    public DebtDto getDebt(@PathVariable("id") @CanAccessDebt Long debtId) {

        return this.debtMapper.toDebtDto(this.debtService.findAndFetchDetails(debtId));
    }

    @PostMapping(Endpoints.DEBTS_SEARCH)
    public PagedModel<DebtDto> searchDebts(@Valid @RequestBody DebtQuery query) {

        return new PagedModel<>(
                this.debtService
                        .search(query)
                        .map(this.debtMapper::toDebtDto)
        );
    }

    @PostMapping(Endpoints.PAY_DEBT)
    public DebtDto pay(@PathVariable Long id, @Valid @RequestBody PayDebtDto dto) {

        return this.debtMapper.toDebtDto(this.debtService.pay(id, dto));
    }

}
