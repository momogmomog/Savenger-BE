package com.momo.savanger.api.util;

import com.momo.savanger.api.user.User;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getCurrentUser() {

        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null) {
            throw ApiException.with(ApiErrorCode.ERR_005);
        }

        final var principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw ApiException.with(ApiErrorCode.ERR_006);
        }

        return (User) principal;
    }

}
