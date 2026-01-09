package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.dto.IModifyTransactionDto;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidTransactionDtoValidator implements
        ConstraintValidator<ValidTransactionDto, IModifyTransactionDto> {

    private final TagService tagService;

    private final CategoryService categoryService;

    @Override
    public void initialize(ValidTransactionDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(IModifyTransactionDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getBudgetId() == null) {
            return true;
        }
        if (!this.categoryService.isCategoryValid(dto.getCategoryId(), dto.getBudgetId())) {
            return ValidationUtil.fail(constraintValidatorContext, "categoryId",
                    ValidationMessages.CATEGORY_DOES_NOT_EXIST_OR_BUDGET_IS_NOT_VALID);
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
