package com.momo.savanger.api.budget;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.momo.savanger.api.budget.constraints.CanAddParticipants;
import com.momo.savanger.api.budget.constraints.EditParticipants;
import com.momo.savanger.api.budget.converter.IAssignParticipantDto;
import com.momo.savanger.api.budget.converter.ValidBudgetConverter;
import com.momo.savanger.constraints.NotNull;
import lombok.Data;

@Data
@EditParticipants(expectedResult = false)
public class AssignParticipantDto implements IAssignParticipantDto {

    @NotNull
    private Long participantId;

    @NotNull
    @JsonProperty("budgetId")
    @ValidBudgetConverter
    @CanAddParticipants
    private Budget budgetRef;
}
