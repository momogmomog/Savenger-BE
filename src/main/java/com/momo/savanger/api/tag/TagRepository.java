package com.momo.savanger.api.tag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryFragment {

    List<Tag> findByBudgetId(Long budgetId);

}
