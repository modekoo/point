package com.musinsa.point;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.point.domain.dto.Point.*;
import com.musinsa.point.domain.dto.pointPolicy.PointPolicyReqDto;
import com.musinsa.point.domain.entity.*;
import com.musinsa.point.domain.enums.PointStatus;
import com.musinsa.point.domain.repository.PointUsageRepository;
import com.musinsa.point.domain.repository.UserPointRepository;
import com.musinsa.point.domain.repository.pointUsageLink.PointUsageLinkRepository;
import com.musinsa.point.domain.service.*;
import com.musinsa.point.dto.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
    @Autowired
    UserPointRepository userPointRepository;
    @Autowired
    PointUsageLinkService pointUsageLinkService;
    @Autowired
    PointUsageService pointUsageService;
    @Autowired
    PointUsageLinkRepository pointUsageLinkRepository;

    @BeforeEach
    public void setup() throws Exception {
        userPointRepository.deleteAll();
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

        Long pointItemKey;

        @BeforeEach
        public void setup() throws Exception {
            PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of("koo", 2000L);
            String reqJsonStr = objectMapper.writeValueAsString(pointEarnReqDto);
            log.debug("reqJsonStr = {}", reqJsonStr);

            MvcResult result = mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk()).andReturn();

            String resultStr = result.getResponse().getContentAsString();
            CommonResponseDto<PointEarnResDto> responseDto = objectMapper.readValue(resultStr, new TypeReference<CommonResponseDto<PointEarnResDto>>(){});
            pointItemKey = responseDto.getResult().pointItemKey();
            log.debug("pointItemKey = {}", pointItemKey);
        }

        @Test
        void setPointCancelTest() throws Exception {
            log.debug("pointItemKey = {}", pointItemKey);
            PointCancelReqDto pointCancelReqDto = PointCancelReqDto.of(pointItemKey);
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

    @Nested
    class PointUse{
        @BeforeEach
        public void setup() throws Exception {
            PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of("koo", 2000L);
            String reqJsonStr = objectMapper.writeValueAsString(pointEarnReqDto);

            mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk());

            PointEarnReqDto pointEarnReqDto2 = PointEarnReqDto.of("koo", 1000L);
            String reqJsonStr2 = objectMapper.writeValueAsString(pointEarnReqDto2);

            mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr2)
            ).andExpect(status().isOk());
        }

        @Test
        public void pointUseTest() throws Exception {
            PointUseReqDto pointUseReqDto = PointUseReqDto.of("test001", 2500L, "koo");
            String reqJsonStr = objectMapper.writeValueAsString(pointUseReqDto);

            UserPointInfo beforeUserPointInfo = userPointInfoService.getUserPointInfo(pointUseReqDto.userId());
            log.debug("이전 포인트 잔량 = {}", beforeUserPointInfo.getPointTotalBalance());

            mockMvc.perform(post("/point/use")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk());

            UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointUseReqDto.userId());
            log.debug("포인트 잔량 = {}", userPointInfo.getPointTotalBalance());

            List<PointUsageLink> pointUsageLinkList = pointUsageLinkService.getPointUsageLinkListByOrderKey(pointUseReqDto.orderKey());
            long usedPointTotal = pointUsageLinkList.stream().mapToLong(PointUsageLink::getPointUsageAmount).sum();
            log.debug("포인트 사용량 = {}", usedPointTotal);

            for(PointUsageLink pointUsageLink : pointUsageLinkList){
                PointItem pointItem = pointUsageLink.getPointItem();
                log.debug("usageLinkId = {}, pointItemKey = {}, pointItemAmount = {}, usageLinkAmount = {}, pointItemStatus = {}"
                        , pointUsageLink.getPointUsageLinkKey(), pointItem.getPointItemKey(), pointItem.getPointAmount(), pointUsageLink.getPointUsageAmount(), pointItem.getPointStatus());
            }
            Assertions.assertEquals(pointUseReqDto.pointUseAmount(), usedPointTotal);
        }

    }

    @Nested
    class PointUseCancel{
        @BeforeEach
        public void setup() throws Exception {
            PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of("koo", 2000L);
            String reqJsonStr = objectMapper.writeValueAsString(pointEarnReqDto);

            mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr)
            ).andExpect(status().isOk());

            PointEarnReqDto pointEarnReqDto2 = PointEarnReqDto.of("koo", 1000L);
            String reqJsonStr2 = objectMapper.writeValueAsString(pointEarnReqDto2);

            mockMvc.perform(post("/point/earn")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr2)
            ).andExpect(status().isOk());

            PointUseReqDto pointUseReqDto = PointUseReqDto.of("test001", 2500L, "koo");
            String reqJsonStr3 = objectMapper.writeValueAsString(pointUseReqDto);

            mockMvc.perform(post("/point/use")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJsonStr3)
            ).andExpect(status().isOk());
        }

        @Test
        public void pointUseCancel() throws Exception{
            PointUseCancelReqDto pointUseCancelReqDto = PointUseCancelReqDto.of("test001", 2300L, "koo");
            String reqJson = objectMapper.writeValueAsString(pointUseCancelReqDto);

            mockMvc.perform(post("/point/use/cancel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(reqJson)
            ).andExpect(status().isOk());

            UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(pointUseCancelReqDto.userId());
            log.debug("포인트 잔량 = {}", userPointInfo.getPointTotalBalance());
            Assertions.assertEquals(1000+2000-2500+2300, userPointInfo.getPointTotalBalance());

            List<PointUsage> pointUsageList = pointUsageService.getPointUsageByOrderKey(pointUseCancelReqDto.orderKey());
            for(PointUsage pointUsage : pointUsageList){
                log.debug("pointUsageKey = {}, pointUsageType = {}, pointUsageAmount = {}", pointUsage.getPointUsageKey(), pointUsage.getPointUsageType(), pointUsage.getPointUsageAmount());
            }
            long pointUsageSum = pointUsageList.stream().mapToLong(PointUsage::getPointUsageAmount).sum();
            log.debug("해당 주문에 사용한 포인트 양(header) = {}", pointUsageSum);
            Assertions.assertEquals(200L, pointUsageSum);

            List<PointUsageLink> pointUsageLinkList = pointUsageLinkService.getPointUsageLinkListByOrderKey(pointUseCancelReqDto.orderKey());
            for(PointUsageLink pointUsageLink : pointUsageLinkList){
                log.debug("pointItemKey = {}, pointStatus = {}, pointAmount = {}, pointUsageLinkKey = {}, pointUsageLinkAmount = {}"
                        , pointUsageLink.getPointItem().getPointItemKey(), pointUsageLink.getPointItem().getPointStatus(), pointUsageLink.getPointItem().getPointAmount()
                        , pointUsageLink.getPointUsageLinkKey(), pointUsageLink.getPointUsageAmount());
            }

            long pointUsageLinkSum = pointUsageLinkList.stream().mapToLong(PointUsageLink::getPointUsageAmount).sum();
            log.debug("해당 주문에 사용한 포인트 양(link) = {}", pointUsageLinkSum);

            Assertions.assertEquals(2500-2300, pointUsageLinkSum);
        }
    }
}
