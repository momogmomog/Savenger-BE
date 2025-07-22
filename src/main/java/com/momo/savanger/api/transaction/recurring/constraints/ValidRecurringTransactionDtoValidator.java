package com.momo.savanger.api.transaction.recurring.constraints;

import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidRecurringTransactionDtoValidator implements
        ConstraintValidator<ValidRecurringTransactionDto,
                CreateRecurringTransactionDto> {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PrepaymentService prepaymentService;


    @Override
    public void initialize(ValidRecurringTransactionDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateRecurringTransactionDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (!this.categoryService.isCategoryValid(dto.getCategoryId(), dto.getBudgetId())) {
            return this.fail(constraintValidatorContext, "categoryId",
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

            return this.fail(constraintValidatorContext, "tagIds",
                    String.format("Invalid tags: %s", invalidIds));
        }

        return true;
    }

    private boolean fail(ConstraintValidatorContext context, String field, String msg) {
        context.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode(field)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;

    }
}
