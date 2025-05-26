package com.momo.savanger.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final FieldErrorMapper fieldErrorMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ConstratintViolationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        final List<FieldErrorDto> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this.fieldErrorMapper::fieldErrorToFieldErrorDto)
                .collect(Collectors.toList());

        final ConstratintViolationErrorResponse response = new ConstratintViolationErrorResponse(
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ConstratintViolationErrorResponse> handleValidationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        final List<FieldErrorDto> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    final String field = this.extractFieldName(violation);
                    final String constraintName = violation.getConstraintDescriptor()
                            .getAnnotation()
                            .annotationType()
                            .getSimpleName();

                    return new FieldErrorDto(
                            violation.getMessage(),
                            field,
                            violation.getInvalidValue(),
                            false,
                            constraintName
                    );
                })
                .collect(Collectors.toList());

        final ConstratintViolationErrorResponse response = new ConstratintViolationErrorResponse(
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    // Helper method to extract the field name from the violation's property path
    private String extractFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiErrorException(ApiException ex,
            HttpServletRequest request) {
        return logAndReturn(
                new ErrorResponse(
                        ex.getErrorCode(),
                        request.getRequestURI(),
                        ex.getData()
                ),
                ex
        );
    }

    private ResponseEntity<ErrorResponse> logAndReturn(ErrorResponse response, Exception ex) {
        log.error(response.toString(), ex);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}
