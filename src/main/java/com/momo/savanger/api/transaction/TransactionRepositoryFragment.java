package com.momo.savanger.api.transaction;

import com.momo.savanger.api.util.SpecificationExecutor;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface TransactionRepositoryFragment extends SpecificationExecutor<Transaction, Long> {

    List<Long> getCategoryIds(Specification<Transaction> specification);

    List<Long> getTagIds(Specification<Transaction> specification);

    BigDecimal sum(Specification<Transaction> specification);
}
