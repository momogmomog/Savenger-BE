package com.momo.savanger.api.budget.converter;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JsonDeserialize(using = ValidBudgetConverterImpl.class)
@JacksonAnnotationsInside
public @interface ValidBudgetConverter {

}
