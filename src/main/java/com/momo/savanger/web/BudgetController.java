package com.momo.savanger.web;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetMapper;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.budget.dto.BudgetDto;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UnassignParticipantDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                this.budgetService.create(createBudgetDto, user.getId())
        );
    }

    @PostMapping(Endpoints.ASSIGN_PARTICIPANT)
    public Integer assignParticipants(@PathVariable Long id,
            @Valid @RequestBody AssignParticipantDto dto) {

        this.budgetService.addParticipant(dto);

        Budget budget = this.budgetService.findById(id);

        return budget.getParticipants().size();
    }

    @DeleteMapping(Endpoints.ASSIGN_PARTICIPANT)
    public Integer unassignParticipants(@PathVariable Long id,
            @Valid @RequestBody UnassignParticipantDto dto) {

        this.budgetService.deleteParticipant(dto);

        Budget budget = this.budgetService.findById(id);

        return budget.getParticipants().size();
    }


}
