package com.point;

import com.point.domain.dto.user.CreateUserReqDto;
import com.point.domain.entity.PointPolicy;
import com.point.domain.entity.User;
import com.point.domain.entity.UserPointInfo;
import com.point.domain.service.PointPolicyService;
import com.point.domain.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTests {

    @Autowired
    PointPolicyService pointPolicyService;
    @Autowired
    UserService userService;

	@Test
    @Transactional
	void createUserAndUserPointInfoTest() throws Exception {
        String userId = UUID.randomUUID().toString().replaceAll("-", "");
        CreateUserReqDto createUserReqDto = new CreateUserReqDto(userId);

        userService.createUser(createUserReqDto);

        PointPolicy pointPolicy = pointPolicyService.getUserPolicy(userId);
        UserPointInfo userPointInfo = pointPolicy.getUserPointInfo();
        User user = userPointInfo.getUser();

        Assertions.assertEquals(userId, user.getUserId());
	}
}
