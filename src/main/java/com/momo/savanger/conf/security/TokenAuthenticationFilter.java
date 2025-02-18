package com.momo.savanger.conf.security;

import com.momo.savanger.api.authtoken.AuthToken;
import com.momo.savanger.api.authtoken.AuthTokenService;
import com.momo.savanger.api.user.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


@RequiredArgsConstructor
public class TokenAuthenticationFilter implements Filter {

    private final AuthTokenService authTokenService;

    private final String authTokenHeaderName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        final String accessToken = httpRequest.getHeader(this.authTokenHeaderName);

        if (accessToken != null) {
            final AuthToken token = this.authTokenService.findById(accessToken);

            if (token != null) {
                if (this.authTokenService.isAuthTokenExpired(token)) {
                    this.authTokenService.remove(token);
                } else {
                    final User user = token.getUser();
                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    this.authTokenService.update(token);
                }
            }
            //TODO you can set to unauthorized if that is what you need.
        }

        chain.doFilter(request, response);
    }
}
