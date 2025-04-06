package com.momo.savanger.api.budget;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toBudget(BudgetDto budgetDto);
}
