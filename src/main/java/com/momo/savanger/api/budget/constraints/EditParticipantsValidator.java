package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.converter.IAssignParticipantDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EditParticipantsValidator implements
        ConstraintValidator<EditParticipants, IAssignParticipantDto> {

    private String message;
    private boolean expectedResult;

    private final BudgetService budgetService;

    private final UserService userService;

    @Override
    public void initialize(EditParticipants constraintAnnotation) {
        message = constraintAnnotation.message();
        expectedResult = constraintAnnotation.expectedResult();
    }

    @Override
    public boolean isValid(IAssignParticipantDto dto,
            ConstraintValidatorContext context) {

        boolean valid = true;

        try {
            if (dto.getBudgetRef() == null) {
                return fail(context, "budgetId", ValidationMessages.INVALID_BUDGET);
            }

            final Budget budget = dto.getBudgetRef();

            final User participant = this.userService.findById(dto.getParticipantId());

            if (participant == null) {
                return this.fail(context, ValidationMessages.USER_DOESNT_EXIST);
            }

            if (Objects.equals(participant.getId(), budget.getOwnerId())) {
                return this.fail(context, ValidationMessages.OWNER_CANNOT_BE_EDIT);
            }

            boolean isParticipantExist = budget.getParticipants().contains(participant);

            if (!expectedResult && isParticipantExist) {
                return this.fail(context, ValidationMessages.ALREADY_PARTICIPANT);
            } else if (expectedResult && !isParticipantExist) {
                return this.fail(context, ValidationMessages.PARTICIPANT_NOT_EXIST);
            }

        } catch (Exception ignored) {
        }

        return valid;

    }

    private boolean fail(ConstraintValidatorContext context, String msg) {
        return this.fail(context, "participantId", msg);
    }

    private boolean fail(ConstraintValidatorContext context, String field, String msg) {
        context.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode(field)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;

    }
}
