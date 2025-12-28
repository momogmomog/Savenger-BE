package com.momo.savanger.web;

import com.momo.savanger.api.budget.BudgetMapper;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.budget.constraints.ValidBudget;
import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.budget.dto.BudgetDto;
import com.momo.savanger.api.budget.dto.BudgetSearchQuery;
import com.momo.savanger.api.budget.dto.BudgetSearchResponseDto;
import com.momo.savanger.api.budget.dto.BudgetStatisticsDto;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UnassignParticipantDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.Endpoints;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
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

    @GetMapping(Endpoints.BUDGET)
    public BudgetDto getBudget(@PathVariable("id") @CanAccessBudget Long budgetId) {
        return this.budgetMapper.toBudgetDto(this.budgetService.findByIdFetchAll(budgetId));
    }

    @GetMapping(Endpoints.BUDGET_STATISTICS)
    public BudgetStatisticsDto getBudgetStatistics(
            @PathVariable("id") @CanAccessBudget Long budgetId) {
        return this.budgetMapper.toStatisticsDto(
                this.budgetService.getStatisticsFetchAll(budgetId)
        );
    }

    @PostMapping(Endpoints.PARTICIPANTS)
    public BudgetDto assignParticipants(@PathVariable @ValidBudget Long id,
            @Valid @RequestBody AssignParticipantDto dto) {

        if (!id.equals(dto.getBudgetRef().getId())) {
            throw ApiException.with(ApiErrorCode.ERR_0001);
        }
        this.budgetService.addParticipant(dto);
        return this.budgetMapper.toBudgetDto(this.budgetService.findByIdFetchAll(id));
    }

    @DeleteMapping(Endpoints.PARTICIPANTS)
    public BudgetDto unassignParticipants(@PathVariable @ValidBudget Long id,
            @Valid @RequestBody UnassignParticipantDto dto) {

        if (!id.equals(dto.getBudgetRef().getId())) {
            throw ApiException.with(ApiErrorCode.ERR_0001);
        }
        this.budgetService.deleteParticipant(dto);
        return this.budgetMapper.toBudgetDto(this.budgetService.findByIdFetchAll(id));
    }

    @PostMapping(Endpoints.BUDGET_SEARCH)
    public PagedModel<BudgetSearchResponseDto> searchBudget(
            @Valid @RequestBody BudgetSearchQuery query
            , @AuthenticationPrincipal User user) {

        return new PagedModel<>(this.budgetService
                .searchBudget(query, user)
                .map(this.budgetMapper::toBudgetSearchResponseDto));
    }


}
