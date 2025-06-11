package com.momo.savanger.api.budget;

import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.budget.dto.BudgetSearchQuery;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.BudgetStatisticsDto;
import com.momo.savanger.api.budget.dto.UnassignParticipantDto;
import com.momo.savanger.api.revision.Revision;
import com.momo.savanger.api.user.User;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BudgetService {

    Budget findById(Long id);

    Budget findByIdFetchAll(Long id);

    Optional<Budget> findIfValid(Long id);

    Budget create(CreateBudgetDto createBudgetDto, Long ownerId);

    boolean isBudgetValid(Long id);

    boolean isUserPermitted(User user, Long budgetId);

    void addParticipant(AssignParticipantDto dto);

    void deleteParticipant(UnassignParticipantDto dto);

    Page<Budget> searchBudget(BudgetSearchQuery query, User user);

    void updateBudgetAfterRevision(Long id, Revision revision);

    BudgetStatisticsDto getStatistics(Long budgetId);

}
