package biz.user.service;

import egovframework.EgovBootApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = EgovBootApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void findUserByName() throws Exception {
        List<String> list = userService.getUserByName("테스트사용자");
        Assertions.assertThat(list).size().isEqualTo(2);
    }

}
