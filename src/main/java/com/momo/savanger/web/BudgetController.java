package com.momo.savanger.web;

import com.momo.savanger.api.budget.BudgetDto;
import com.momo.savanger.api.budget.BudgetMapper;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.CreateBudgetDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class BudgetController {

    private final BudgetService budgetService;

    private final BudgetMapper budgetMapper;

    @PostMapping(Endpoints.BUDGETS)
    public BudgetDto create(@Valid @RequestBody CreateBudgetDto createBudgetDto,
            @AuthenticationPrincipal User user) {

        return this.budgetMapper.toBudgetDto(
                this.budgetService.saveBudget(createBudgetDto, user.getId())
        );
    }
}
