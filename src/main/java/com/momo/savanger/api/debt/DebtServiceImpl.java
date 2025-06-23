package com.momo.savanger.api.debt;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;

    private final DebtMapper debtMapper;

    @Override
    public Debt findById(Long id) {
        return this.debtRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0013));
    }

    @Override
    public Debt create(CreateDebtDto dto) {
        Debt debt = this.debtMapper.toDebt(dto);

        debt.setCreateDate(LocalDateTime.now());
        debt.setUpdateDate(LocalDateTime.now());

        this.debtRepository.saveAndFlush(debt);

        return this.findById(debt.getId());
    }

    @Override
    public Boolean validDebt(CreateDebtDto dto) {
        final Specification<Debt> specification = DebtSpecifications.lenderBudgetIdEquals(
                        dto.getLenderBudgetId())
                .and(DebtSpecifications.receiverBudgetIdEquals(dto.getReceiverBudgetId()));

        return !this.debtRepository.exists(specification);
    }
}
