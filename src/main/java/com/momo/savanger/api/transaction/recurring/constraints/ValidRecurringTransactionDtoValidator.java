package com.momo.savanger.api.transaction.recurring.constraints;

import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidRecurringTransactionDtoValidator implements
        ConstraintValidator<ValidRecurringTransactionDto,
                CreateRecurringTransactionDto> {

    private final CategoryService categoryService;

    private final TagService tagService;

    private final DebtService debtService;

    @Override
    public void initialize(ValidRecurringTransactionDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateRecurringTransactionDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getBudgetId() == null) {
            return true;
        }

        if (dto.getCategoryId() != null) {
            if (!this.categoryService.isCategoryValid(dto.getCategoryId(), dto.getBudgetId())) {
                return ValidationUtil.fail(constraintValidatorContext, "categoryId",
                        ValidationMessages.CATEGORY_DOES_NOT_EXIST_OR_BUDGET_IS_NOT_VALID);
            }
        }

        if (dto.getDebtId() != null) {
            if (!this.debtService.isValid(dto.getDebtId(), dto.getBudgetId())) {
                return ValidationUtil.fail(constraintValidatorContext, "debtId",
                        ValidationMessages.DEBT_IS_NOT_VALID);
            }
        }

        if (dto.getTagIds() == null) {
            return true;
        }

        final List<Tag> tags = this.tagService.findByBudgetAndIdContaining(dto.getTagIds(),
                dto.getBudgetId());

        if (tags.size() != dto.getTagIds().size()) {
            final List<Long> invalidIds = dto
                    .getTagIds()
                    .stream()
                    .filter(tid -> tags.stream().noneMatch(tag -> tag.getId().equals(tid)))
                    .toList();

            return ValidationUtil.fail(constraintValidatorContext, "tagIds",
                    String.format("Invalid tags: %s", invalidIds));
        }

        return true;
    }
}
