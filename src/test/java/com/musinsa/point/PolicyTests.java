package com.musinsa.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.entity.User;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.repository.PointPolicyRepository;
import com.musinsa.point.domain.repository.UserPointRepository;
import com.musinsa.point.domain.repository.UserRepository;
import com.musinsa.point.domain.service.PointPolicyService;
import com.musinsa.point.domain.service.UserPointInfoService;
import com.musinsa.point.exception.ApiException;
import com.musinsa.point.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PolicyTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PointPolicyService pointPolicyService;

    @BeforeAll
    public void setup() throws Exception {
        String reqJsonStr = """
                {"userId":"koo"}
                """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());
    }

	@Test
	void setPointPolicy() throws Exception {

        PointPolicyReqDto pointPolicyReqDto = PointPolicyReqDto.of("koo", 3000L, 50000L);
        ObjectMapper mapper = new ObjectMapper();

        String reqJsonStr = mapper.writeValueAsString(pointPolicyReqDto);
        log.debug("reqJsonStr = {}", reqJsonStr);

        mockMvc.perform(put("/point/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());

        PointPolicy pointPolicy  = pointPolicyService.getUserPolicy(pointPolicyReqDto.userId());
        Assertions.assertEquals(pointPolicy.getPointEarnLimit(), pointPolicyReqDto.pointEarnLimit());
        Assertions.assertEquals(pointPolicy.getPointTotalLimit(), pointPolicyReqDto.pointTotalLimit());
	}

    @Test
    void setPointPolicyEarnLimitNullTest() throws Exception {

        PointPolicyReqDto pointPolicyReqDto = PointPolicyReqDto.of("koo", null, 50000L);
        ObjectMapper mapper = new ObjectMapper();

        String reqJsonStr = mapper.writeValueAsString(pointPolicyReqDto);
        log.debug("reqJsonStr = {}", reqJsonStr);

        mockMvc.perform(put("/point/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());

        PointPolicy pointPolicy  = pointPolicyService.getUserPolicy(pointPolicyReqDto.userId());
        Assertions.assertNotEquals(null, pointPolicy.getPointEarnLimit());
        Assertions.assertEquals(pointPolicy.getPointTotalLimit(), pointPolicyReqDto.pointTotalLimit());
        log.debug("pointPolicy.getPointEarnLimit = {}", pointPolicy.getPointEarnLimit());
    }

    @Test
    void setPointPolicyTotalLimitNullTest() throws Exception {

        PointPolicyReqDto pointPolicyReqDto = PointPolicyReqDto.of("koo", 5000L, null);
        ObjectMapper mapper = new ObjectMapper();

        String reqJsonStr = mapper.writeValueAsString(pointPolicyReqDto);
        log.debug("reqJsonStr = {}", reqJsonStr);

        mockMvc.perform(put("/point/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());

        PointPolicy pointPolicy  = pointPolicyService.getUserPolicy(pointPolicyReqDto.userId());
        Assertions.assertEquals(pointPolicyReqDto.pointEarnLimit(), pointPolicy.getPointEarnLimit());
        Assertions.assertNotEquals(null, pointPolicy.getPointTotalLimit());
        log.debug("pointPolicy.getPointTotalLimit = {}", pointPolicy.getPointTotalLimit());
    }
}
