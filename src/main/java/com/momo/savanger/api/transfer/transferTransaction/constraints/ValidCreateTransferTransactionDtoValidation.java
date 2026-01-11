package com.momo.savanger.api.transfer.transferTransaction.constraints;

import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidCreateTransferTransactionDtoValidation implements
        ConstraintValidator<ValidCreateTransferTransactionDto, CreateTransferTransactionDto> {

    private final TransferService transferService;


    @Override
    public void initialize(ValidCreateTransferTransactionDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateTransferTransactionDto dto,
            ConstraintValidatorContext constraintValidatorContext) {
        Transfer transfer = this.transferService.getById(dto.getTransferId());

        if (!transfer.getSourceBudgetId().equals(dto.getSourceCategoryId())) {
            ValidationUtil.fail(constraintValidatorContext, "sourceCategoryId",
                    String.format(ValidationMessages.CATEGORY_IS_NOT_VALID,
                            dto.getSourceCategoryId()));
        }

        if (!transfer.getReceiverBudgetId().equals(dto.getReceiverCategoryId())) {
            ValidationUtil.fail(constraintValidatorContext, "receiverCategoryId",
                    String.format(ValidationMessages.CATEGORY_IS_NOT_VALID,
                            dto.getSourceCategoryId()));
        }

        return true;
    }
}
