package com.musinsa.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.point.domain.dto.Point.PointEarnReqDto;
import com.musinsa.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.service.PointItemService;
import com.musinsa.point.domain.service.PointPolicyService;
import com.musinsa.point.domain.service.UserPointInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class PointTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserPointInfoService userPointInfoService;
    @Autowired
    PointItemService pointItemService;
    @Autowired
    PointPolicyService pointPolicyService;

    @BeforeEach
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
	void setPointEarn() throws Exception {

        PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of("koo", 2000L);
        ObjectMapper mapper = new ObjectMapper();
        UserPointInfo beforeUserPointInfo = userPointInfoService.getUserPointInfo(pointEarnReqDto.userId());
        PointPolicy policy = pointPolicyService.getUserPolicy(pointEarnReqDto.userId());

        log.debug("1회 한도 금액 = {},", policy.getPointEarnLimit());

        String reqJsonStr = mapper.writeValueAsString(pointEarnReqDto);
        log.debug("reqJsonStr = {}", reqJsonStr);

        mockMvc.perform(post("/point/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());

        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointEarnReqDto.userId());

        //이전 금액과 비교 balance
        Assertions.assertEquals(userPointInfo.getPointTotalBalance(), beforeUserPointInfo.getPointTotalBalance() + pointEarnReqDto.pointAmount());
        List<PointItem> pointItemList = pointItemService.getPointItemList(pointEarnReqDto.userId());

        if(CollectionUtils.isEmpty(pointItemList))
            Assertions.fail();

        Assertions.assertEquals(pointItemList.getFirst().getPointAmount(), pointEarnReqDto.pointAmount());

	}
}
