package com.momo.savanger.api.user;

import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Optional<User> findById(Long id);

    User getById(Long id);

    User getByUsername(String username);
}
