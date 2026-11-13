package com.momo.savanger.api.prepayment.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class IsBudgetBalanceBiggerValidator implements
        ConstraintValidator<IsBudgetBalanceBigger, CreatePrepaymentDto> {

    private final BudgetService budgetService;

    private final PrepaymentService prepaymentService;


    @Override
    public void initialize(IsBudgetBalanceBigger constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreatePrepaymentDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getBudgetId() == null || dto.getAmount() == null) {
            return true;
        }

        final BudgetStatistics statistics = budgetService.getStatistics(dto.getBudgetId());

        BigDecimal availableBalance = statistics.getRealBalance();

        final Prepayment existingPrepayment = this.getExistingPrepaymentIfEdit();
        if (existingPrepayment != null
                && existingPrepayment.getBudgetId().equals(dto.getBudgetId())) {
            availableBalance = availableBalance.add(existingPrepayment.getRemainingAmount());
        }

        if (availableBalance.compareTo(dto.getAmount()) < 0) {
            return ValidationUtil.fail(constraintValidatorContext, "amount",
                    ValidationMessages.AMOUNT_CANNOT_BE_BIGGER_THAN_BALANCE);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private Prepayment getExistingPrepaymentIfEdit() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }

        final HttpServletRequest request = attrs.getRequest();
        final Object uriVarsObj = request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
        );
        if (!(uriVarsObj instanceof Map<?, ?> uriVars)) {
            return null;
        }

        final Object idObj = ((Map<String, Object>) uriVars).get("id");
        if (idObj == null) {
            return null;
        }

        try {
            final Long id = Long.valueOf(idObj.toString());
            return this.prepaymentService.findByIdIfExists(id).orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
