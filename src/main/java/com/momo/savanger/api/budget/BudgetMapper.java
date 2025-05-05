package com.momo.savanger.api.budget;

import com.momo.savanger.api.budget.dto.BudgetDto;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toBudget(CreateBudgetDto createBudgetDto);

    BudgetDto toBudgetDto(Budget budget);
}
