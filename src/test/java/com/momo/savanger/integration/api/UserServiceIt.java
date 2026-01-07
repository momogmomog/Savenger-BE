package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import com.momo.savanger.util.AssertUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class UserServiceIt {

    @Autowired
    private UserService userService;

    @Test
    public void testFindById_validId_shouldReturnUser() {
        User user = this.userService.findById(2L).orElse(null);

        assertNotNull(user);
    }

    @Test
    public void testFindById_invalidId_shouldReturnNull() {
        User user = this.userService.findById(989L).orElse(null);

        assertNull(user);
    }

    @Test
    public void testGetById_validId_shouldReturnUser() {
        User user = this.userService.getById(2L);

        assertNotNull(user);
    }

    @Test
    public void testGetById_invalidId_shouldReturnNull() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0009, () -> this.userService.getById(989L));
    }

    @Test
    public void testGetByUsername_validUsername_shouldReturnUser() {
        User user = this.userService.getByUsername("Roza");

        assertNotNull(user);
    }

    @Test
    public void testGetByUsername_invalidUsername_shouldReturnNull() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0009, () -> this.userService.getByUsername("Godji"));
    }

    @Test
    public void testLoadUserByUsername_validUsername_shouldReturnUser() {
        UserDetails user = this.userService.loadUserByUsername("Roza");

        assertNotNull(user);
    }

    @Test
    public void testLoadUserByUsername_invalidUsername_shouldReturnNull() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0009, () -> this.userService.loadUserByUsername("Godji"));
    }


}
