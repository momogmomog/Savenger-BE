package com.momo.savanger.api.authtoken;

import com.momo.savanger.api.user.User;
import java.util.List;

public interface AuthTokenService {

    void update(AuthToken token);

    void remove(AuthToken authToken);

    boolean isAuthTokenExpired(AuthToken token);

    AuthToken createToken(User loggedInUser);

    AuthToken findById(String id);

    List<AuthToken> findByUser(User user);
}
