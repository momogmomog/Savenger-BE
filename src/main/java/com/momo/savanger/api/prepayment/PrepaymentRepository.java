package com.momo.savanger.api.prepayment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrepaymentRepository extends JpaRepository<Prepayment, Long>,
        PrepaymentRepositoryFragment {

}
