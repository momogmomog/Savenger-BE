package com.momo.savanger.api.budget;

import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.budget.dto.BudgetSearchQuery;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UnassignParticipantDto;
import com.momo.savanger.api.budget.dto.UpdateBudgetDto;
import com.momo.savanger.api.revision.Revision;
import com.momo.savanger.api.user.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BudgetService {

    Budget findById(Long id);

    Budget findByIdFetchAll(Long id);

    Optional<Budget> findIfValid(Long id);

    Budget create(CreateBudgetDto createBudgetDto, Long ownerId);

    Budget update(UpdateBudgetDto updateBudgetDto, Long budgetId);

    boolean isBudgetValid(Long id);

    boolean isUserPermitted(User user, Long budgetId);

    boolean isUserPermitted(User user, Long budgetId, boolean filterOnlyActiveBudgets);

    void addParticipant(AssignParticipantDto dto);

    void deleteParticipant(UnassignParticipantDto dto);

    Page<Budget> searchBudget(BudgetSearchQuery query, User user);

    void updateBudgetAfterRevision(Long id, Revision revision);

    BudgetStatistics getStatistics(Long budgetId);

    BudgetStatistics getStatisticsFetchAll(Long budgetId);

    boolean isBudgetDateBefore(Long budgetId, LocalDateTime localDate);
}
