package com.momo.savanger.integration.web;

import static com.momo.savanger.integration.web.Constants.FIRST_USER_USERNAME;
import static com.momo.savanger.integration.web.Constants.SECOND_USER_USERNAME;

import com.momo.savanger.api.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithLocalLoggedUserFactory implements WithSecurityContextFactory<WithLocalMockedUser> {

    @Override
    public SecurityContext createSecurityContext(WithLocalMockedUser customUser) {
        final User user = new User();
        user.setUsername(customUser.username());

        final String password;
        switch (customUser.username()) {
            case FIRST_USER_USERNAME:
                password = Constants.FIRST_USER_PASSWORD;
                user.setId(1L);
                break;
            case SECOND_USER_USERNAME:
                password = Constants.SECOND_USER_PASSWORD;
                user.setId(2L);
                break;
            default:
                password = customUser.password();
        }

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
