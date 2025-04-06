package com.momo.savanger.web;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetDto;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping(Endpoints.BUDGETS)
    public String create(@Valid BudgetDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return bindingResult.getAllErrors().toString();
        }
        Budget budget = this.budgetService.saveBudget(dto);
        return budget.toString();

    }


}
