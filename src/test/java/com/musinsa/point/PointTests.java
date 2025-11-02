package com.musinsa.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.point.domain.dto.Point.PointCancelReqDto;
import com.musinsa.point.domain.dto.Point.PointEarnReqDto;
import com.musinsa.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.musinsa.point.domain.entity.PointItem;
import com.musinsa.point.domain.entity.PointPolicy;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.enums.PointStatus;
import com.musinsa.point.domain.service.PointItemService;
import com.musinsa.point.domain.service.PointPolicyService;
import com.musinsa.point.domain.service.UserPointInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PointTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserPointInfoService userPointInfoService;
    @Autowired
    PointItemService pointItemService;
    @Autowired
    PointPolicyService pointPolicyService;
    @Autowired
    ObjectMapper objectMapper;

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

    @Nested
    class pointEarn {

        @Test
        void setPointEarnTest() throws Exception {
            PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of("koo", 2000L);
            UserPointInfo beforeUserPointInfo = userPointInfoService.getUserPointInfo(pointEarnReqDto.userId());
            PointPolicy policy = pointPolicyService.getUserPolicy(pointEarnReqDto.userId());

            long beforeUserPointEarnLimit = policy.getPointEarnLimit();
            long beforeUserPointBalance = beforeUserPointInfo.getPointTotalBalance();

            String reqJsonStr = objectMapper.writeValueAsString(pointEarnReqDto);
            log.debug("reqJsonStr = {}", reqJsonStr);

            mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk());

            UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointEarnReqDto.userId());

            log.debug("1회 한도 금액 = {},", beforeUserPointEarnLimit);
            log.debug("이전 잔여 금액 = {}", beforeUserPointBalance);

            //이전 금액과 비교 balance
            Assertions.assertEquals(userPointInfo.getPointTotalBalance(), beforeUserPointBalance + pointEarnReqDto.pointAmount());
            List<PointItem> pointItemList = pointItemService.getPointItemList(pointEarnReqDto.userId());

            if (CollectionUtils.isEmpty(pointItemList))
                Assertions.fail();

            Assertions.assertEquals(pointItemList.getFirst().getPointAmount(), pointEarnReqDto.pointAmount());
        }
    }

    @Nested
    class PointCancel{

        @BeforeEach
        public void setup() throws Exception {
            PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of("koo", 2000L);
            String reqJsonStr = objectMapper.writeValueAsString(pointEarnReqDto);
            log.debug("reqJsonStr = {}", reqJsonStr);

            mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk());
        }

        @Test
        void setPointCancelTest() throws Exception {
            PointCancelReqDto pointCancelReqDto = PointCancelReqDto.of(1L);
            String reqJsonStr = objectMapper.writeValueAsString(pointCancelReqDto);

            mockMvc.perform(put("/point/cancel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk());

            PointItem pointItem = pointItemService.getPointItem(pointCancelReqDto.pointItemKey());
            log.debug("point satatus = {}", pointItem.getPointStatus().name());

            Assertions.assertEquals(PointStatus.CANCELED, pointItem.getPointStatus());
        }
    }
}
