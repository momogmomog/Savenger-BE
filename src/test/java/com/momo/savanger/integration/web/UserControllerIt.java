package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserRepository;
import com.momo.savanger.constants.Endpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerIt extends BaseControllerIt {

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetDetails() throws Exception {

        User user = this.userRepository.findById(1L).orElse(null);
        System.out.println(user.getDateRegistered());

        super.getOK(Endpoints.USER_DETAILS,
                jsonPath("$.id", is(1)),
                jsonPath("$.username", is("Ignat"))
        );
    }

    @Test
    public void testGetDetails_withoutMockedUser() throws Exception {

        super.get(Endpoints.USER_DETAILS, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetOtherUser() throws Exception {

        super.getOK("/user-details/Coco",
                jsonPath("$.id", is(3)),
                jsonPath("$.username", is("Coco"))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetOtherUser_invalidUsername() throws Exception {

        super.get("/user-details/momo", HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetOtherUser_withNoMockedUser() throws Exception {

        super.get("/user-details/Coco", HttpStatus.UNAUTHORIZED);
    }
}
