package com.momo.savanger.api.authtoken;

import com.momo.savanger.api.user.User;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthTokenRepository repository;

    @Value("${authtoken.max.inactivity.minutes}")
    private final int maxTokenInactivityMin;

    @Override
    public void update(AuthToken token) {
        token.setLastAccessTime(LocalDateTime.now());
        this.repository.save(token);
    }

    @Override
    public void remove(AuthToken authToken) {
        this.repository.delete(authToken);
    }

    @Override
    public boolean isAuthTokenExpired(AuthToken token) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime lastAccessTime = token.getLastAccessTime();

        return Math.abs(ChronoUnit.MINUTES.between(now, lastAccessTime))
                > this.maxTokenInactivityMin;
    }

    @Override
    public AuthToken createToken(User loggedInUser) {
        final AuthToken token = new AuthToken();
        token.setUser(loggedInUser);
        token.setLastAccessTime(LocalDateTime.now());

        this.repository.saveAndFlush(token);

        return token;
    }

    @Override
    public AuthToken findById(String id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public List<AuthToken> findByUser(User user) {
        return this.repository.findByUser(user);
    }
}
