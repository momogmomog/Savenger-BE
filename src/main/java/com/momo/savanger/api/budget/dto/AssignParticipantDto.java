package com.momo.savanger.api.budget.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.constraints.AssignParticipantValidation;
import com.momo.savanger.api.budget.constraints.IsBudgetOwner;
import com.momo.savanger.api.budget.converter.ValidBudgetConverter;
import com.momo.savanger.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AssignParticipantValidation(requireUserAssigned = false)
public class AssignParticipantDto implements IAssignParticipantDto {

    @NotNull
    private Long participantId;

    @NotNull
    @JsonProperty("budgetId")
    @ValidBudgetConverter
    @IsBudgetOwner
    private Budget budgetRef;
}
