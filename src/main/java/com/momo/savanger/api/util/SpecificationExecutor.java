package com.momo.savanger.api.util;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public interface SpecificationExecutor<T, ID> {

    boolean exists(Specification<T> specification);

    Page<T> findAll(Specification<T> specification,
            PageQuery pageQuery,
            @Nullable String entityGraph);

    List<T> findAll(Specification<T> specification, @Nullable String entityGraph);
}
