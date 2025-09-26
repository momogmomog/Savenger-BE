package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.dto.IAssignParticipantDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AssignParticipantValidationValidator implements
        ConstraintValidator<AssignParticipantValidation, IAssignParticipantDto> {

    private boolean requiredUserAssigned;

    private final UserService userService;

    @Override
    public void initialize(AssignParticipantValidation constraintAnnotation) {
        requiredUserAssigned = constraintAnnotation.requireUserAssigned();
    }

    @Override
    public boolean isValid(IAssignParticipantDto dto,
            ConstraintValidatorContext context) {

        try {
            if (dto.getBudgetRef() == null) {
                return ValidationUtil.fail(context, "budgetId",
                        ValidationMessages.INVALID_BUDGET);
            }

            final Budget budget = dto.getBudgetRef();

            final Optional<User> maybeParticipant = this.userService.findById(
                    dto.getParticipantId());

            if (maybeParticipant.isEmpty()) {
                return this.fail(context, ValidationMessages.USER_DOESNT_EXIST);
            }

            final User participant = maybeParticipant.get();

            if (Objects.equals(participant.getId(), budget.getOwnerId())) {
                return this.fail(context, ValidationMessages.OWNER_CANNOT_BE_EDIT);
            }

            boolean isParticipantExist = budget.getParticipants().contains(participant);

            if (!requiredUserAssigned && isParticipantExist) {
                return this.fail(context, ValidationMessages.ALREADY_PARTICIPANT);
            } else if (requiredUserAssigned && !isParticipantExist) {
                return this.fail(context, ValidationMessages.PARTICIPANT_NOT_EXIST);
            }

        } catch (Exception ignored) {
        }

        return true;
    }

    private boolean fail(ConstraintValidatorContext context, String msg) {
        return ValidationUtil.fail(context, "participantId", msg);
    }


}
