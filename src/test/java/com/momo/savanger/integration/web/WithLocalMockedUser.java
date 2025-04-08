package com.momo.savanger.integration.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithLocalLoggedUserFactory.class)
public @interface WithLocalMockedUser {

    //Make sure there is user in H2 with this email.
    String username() default Constants.FIRST_USER_USERNAME;

    String password() default Constants.FIRST_USER_PASSWORD;
}
