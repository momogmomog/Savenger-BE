package com.momo.savanger.api.user;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = this.userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("%s does not exist!", username));
        }

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public User getById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_009));
    }
}
