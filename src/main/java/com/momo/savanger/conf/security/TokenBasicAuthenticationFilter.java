package com.momo.savanger.conf.security;

import com.momo.savanger.api.authtoken.AuthToken;
import com.momo.savanger.api.authtoken.AuthTokenService;
import com.momo.savanger.api.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


public class TokenBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthTokenService authTokenService;

    private final String authTokenHeaderName;

    public TokenBasicAuthenticationFilter(AuthenticationManager authenticationManager,
            AuthTokenService authTokenService,
            String authTokenHeaderName) {
        super(authenticationManager);
        this.authTokenService = authTokenService;
        this.authTokenHeaderName = authTokenHeaderName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authResult) {
        //Get user.
        final User user = (User) authResult.getPrincipal();

        //Remove previous tokens.
        this.authTokenService.findByUser(user).forEach(this.authTokenService::remove);

        //Generate new token.
        final AuthToken newToken = this.authTokenService.createToken(user);

        //Add token to session.
        request.getSession(true).setAttribute(this.authTokenHeaderName, newToken.getId());
    }
}
