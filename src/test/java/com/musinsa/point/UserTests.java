package com.musinsa.point;

import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.entity.User;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.service.PointPolicyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PointPolicyService pointPolicyService;

    @BeforeEach
    void cleanup(){
    }

	@Test
	void createUserAndUserPointInfoTest() throws Exception {

        String reqJsonStr = """
                {"userId":"koo"}
                """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());

        PointPolicy pointPolicy = pointPolicyService.getUserPolicy("koo");
        UserPointInfo userPointInfo = pointPolicy.getUserPointInfo();
        User user = userPointInfo.getUser();

        Assertions.assertEquals("koo", user.getUserId());

	}
}
