package com.momo.savanger.api.budget;

import com.momo.savanger.api.budget.dto.BudgetDto;
import com.momo.savanger.api.budget.dto.BudgetSearchResponseDto;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.BudgetStatisticsDto;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toBudget(CreateBudgetDto createBudgetDto);

    BudgetDto toBudgetDto(Budget budget);

    BudgetSearchResponseDto toBudgetSearchResponseDto(Budget budget);

    BudgetStatisticsDto toStatisticsDto(BudgetStatistics statistics);
}
