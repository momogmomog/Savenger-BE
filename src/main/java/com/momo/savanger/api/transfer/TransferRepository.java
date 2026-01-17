package com.momo.savanger.api.transfer;

import com.momo.savanger.constants.EntityGraphs;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>,
        TransferRepositoryFragment {

    @EntityGraph(EntityGraphs.TRANSFER_ALL)
    Optional<Transfer> findTransferById(Long id);

}
