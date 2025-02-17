package com.momo.savanger.api.authtoken;


import com.momo.savanger.api.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {

    List<AuthToken> findByUser(User user);
}
