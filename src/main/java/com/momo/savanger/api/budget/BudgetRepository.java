package com.momo.savanger.api.budget;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetRepositoryFragment {

    boolean existsByIdAndDateStartedLessThanEqual(Long id, LocalDateTime date);
}
