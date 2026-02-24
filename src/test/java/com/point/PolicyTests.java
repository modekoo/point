package com.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.point.domain.dto.user.CreateUserReqDto;
import com.point.domain.entity.PointPolicy;
import com.point.domain.service.PointPolicyService;
import com.point.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PointPolicyService pointPolicyService;
    @Autowired
    UserService userService;

    String userId;

    @BeforeEach
    public void setup() throws Exception {
        userId = UUID.randomUUID().toString().replaceAll("-", "");
        CreateUserReqDto createUserReqDto = new CreateUserReqDto(userId);
        userService.createUser(createUserReqDto);
    }

    private void setPointPolicy(String userId, Long pointEarnLimit, Long pointTotalLimit) throws Exception{
        PointPolicyReqDto pointPolicyReqDto = PointPolicyReqDto.of(userId, pointEarnLimit, pointTotalLimit);
        ObjectMapper mapper = new ObjectMapper();

        String reqJsonStr = mapper.writeValueAsString(pointPolicyReqDto);
        log.debug("reqJsonStr = {}", reqJsonStr);

        mockMvc.perform(put("/point/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());
    }

	@Test
    @Transactional
	void setPointPolicyTest() throws Exception {

        Long pointEarnLimit = 3000L;
        Long pointTotalLimit = 10_000L;
        setPointPolicy(userId, pointEarnLimit, pointTotalLimit);

        PointPolicy pointPolicy  = pointPolicyService.getUserPolicy(userId);
        Assertions.assertEquals(pointEarnLimit, pointPolicy.getPointEarnLimit());
        Assertions.assertEquals(pointTotalLimit, pointPolicy.getPointTotalLimit());
	}

    @Test
    @Transactional
    void setPointPolicyEarnLimitNullTest() throws Exception {

        Long pointEarnLimit = null;
        Long pointTotalLimit = 50_000L;
        setPointPolicy(userId, pointEarnLimit, pointTotalLimit);

        PointPolicy pointPolicy  = pointPolicyService.getUserPolicy(userId);
        Assertions.assertNotEquals(null, pointPolicy.getPointEarnLimit());
        Assertions.assertEquals(pointTotalLimit, pointPolicy.getPointTotalLimit());
        log.debug("pointPolicy.getPointEarnLimit = {}", pointPolicy.getPointEarnLimit());
    }

    @Test
    @Transactional
    void setPointPolicyTotalLimitNullTest() throws Exception {

        Long pointEarnLimit = 5000L;
        Long pointTotalLimit = null;
        setPointPolicy(userId, pointEarnLimit, pointTotalLimit);

        PointPolicy pointPolicy  = pointPolicyService.getUserPolicy(userId);
        Assertions.assertEquals(pointEarnLimit, pointPolicy.getPointEarnLimit());
        Assertions.assertNotEquals(null, pointPolicy.getPointTotalLimit());
        log.debug("pointPolicy.getPointTotalLimit = {}", pointPolicy.getPointTotalLimit());
    }
}
