package com.momo.savanger.api.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {


    User findById(Long id);
}
