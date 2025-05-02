package com.momo.savanger.api.budget;

import com.momo.savanger.api.user.User;
import java.util.Optional;

public interface BudgetService {

    Budget findById(Long id);

    Optional<Budget> findIfValid(Long id);

    Budget create(CreateBudgetDto createBudgetDto, Long ownerId);

    boolean isBudgetValid(Long id);

    boolean isUserPermitted(User user, Long budgetId);

    void addParticipant(AssignParticipantDto dto);

    void deleteParticipant(UnassignParticipantDto dto);
}
