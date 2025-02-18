package com.momo.savanger.web;

import com.momo.savanger.constants.Endpoints;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    @Value("${header.authtoken.name}")
    private final String authTokenName;

    @Value("${authtoken.max.inactivity.minutes}")
    private final int maxInactiveMin;

    @PostMapping(Endpoints.LOGIN)
    @PreAuthorize("isFullyAuthenticated()")
    public LoginSuccessDto authorizeAction(HttpSession session) {
        return new LoginSuccessDto(
                session.getAttribute(this.authTokenName) + "",
                this.maxInactiveMin
        );
    }

    public record LoginSuccessDto(String authToken, int maxInactiveMinutes) {

    }
}
