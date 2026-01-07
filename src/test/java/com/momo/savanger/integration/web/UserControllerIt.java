package com.momo.savanger.integration.web;

import com.momo.savanger.constants.Endpoints;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetDetails() throws Exception {

        this.getOK(Endpoints.USER_DETAILS);
    }

    @Test
    public void testGetDetails_withoutMockedUser() throws Exception {

        this.get(Endpoints.USER_DETAILS, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetOtherUser() throws Exception {

        this.getOK("/user-details/Coco");
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetOtherUser_invalidUsername() throws Exception {

        this.get("/user-details/momo", HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetOtherUser_withNoMockedUser() throws Exception {

        this.get("/user-details/Coco", HttpStatus.UNAUTHORIZED);
    }
}
