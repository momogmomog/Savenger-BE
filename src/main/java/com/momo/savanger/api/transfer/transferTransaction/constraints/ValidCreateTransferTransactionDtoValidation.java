package com.momo.savanger.api.transfer.transferTransaction.constraints;

import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidCreateTransferTransactionDtoValidation implements
        ConstraintValidator<ValidCreateTransferTransactionDto, CreateTransferTransactionDto> {

    private final TransferService transferService;

    private final CategoryService categoryService;


    @Override
    public void initialize(ValidCreateTransferTransactionDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateTransferTransactionDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getTransferId() == null) {
            return true;
        }

        Optional<ApiException> exception = ApiException.tryCatch(
                ApiErrorCode.ERR_0018,
                () -> this.transferService.getById(dto.getTransferId())
        );

        if (exception.isPresent()) {
            return false;
        }

        final Transfer transfer = this.transferService.getById(dto.getTransferId());

        if (dto.getReceiverCategoryId() == null || dto.getSourceCategoryId() == null) {
            return true;
        }

        if (!this.categoryService.isCategoryValid(dto.getSourceCategoryId(),
                transfer.getSourceBudgetId())
        ) {
            return ValidationUtil.fail(constraintValidatorContext,
                    "sourceCategoryId",
                    String.format(ValidationMessages.CATEGORY_IS_NOT_VALID,
                            dto.getSourceCategoryId()
                    )
            );
        }

        if (!this.categoryService.isCategoryValid(dto.getReceiverCategoryId(),
                transfer.getReceiverBudgetId())
        ) {
            return ValidationUtil.fail(constraintValidatorContext,
                    "receiverCategoryId",
                    String.format(ValidationMessages.CATEGORY_IS_NOT_VALID,
                            dto.getReceiverCategoryId()
                    )
            );
        }

        return true;
    }
}
