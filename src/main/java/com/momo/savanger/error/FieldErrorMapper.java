package com.momo.savanger.error;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.validation.FieldError;

@Mapper(componentModel = "spring")
public interface FieldErrorMapper {

    @Mappings({
            @Mapping(source = "defaultMessage", target = "message"),
            @Mapping(source = "code", target = "constraintName"),
    })
    FieldErrorDto fieldErrorToFieldErrorDto(FieldError fieldError);
}
