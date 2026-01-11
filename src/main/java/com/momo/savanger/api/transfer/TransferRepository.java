package com.momo.savanger.api.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>,
        TransferRepositoryFragment {

}
