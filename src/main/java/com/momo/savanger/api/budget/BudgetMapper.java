package com.momo.savanger.api.budget;

import com.momo.savanger.api.budget.dto.BudgetDto;
import com.momo.savanger.api.budget.dto.BudgetSearchResponseDto;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.BudgetStatisticsDto;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UpdateBudgetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toBudget(CreateBudgetDto createBudgetDto);


    Budget mergeIntoBudget(UpdateBudgetDto updateBudgetDto, @MappingTarget Budget budget);

    BudgetDto toBudgetDto(Budget budget);

    BudgetSearchResponseDto toBudgetSearchResponseDto(Budget budget);

    BudgetStatisticsDto toStatisticsDto(BudgetStatistics statistics);
}
