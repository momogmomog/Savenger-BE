package com.momo.savanger.api.budget;

import com.momo.savanger.api.budget.dto.BudgetFullDto;
import com.momo.savanger.api.budget.dto.BudgetSimpleDto;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.BudgetStatisticsDto;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UpdateBudgetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toBudget(CreateBudgetDto createBudgetDto);

    void mergeIntoBudget(UpdateBudgetDto updateBudgetDto, @MappingTarget Budget budget);

    BudgetFullDto toBudgetDto(Budget budget);

    BudgetSimpleDto toBudgetSearchResponseDto(Budget budget);

    BudgetStatisticsDto toStatisticsDto(BudgetStatistics statistics);
}
