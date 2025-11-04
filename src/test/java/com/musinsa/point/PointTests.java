package com.musinsa.point;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.point.domain.dto.Point.*;
import com.musinsa.point.domain.entity.*;
import com.musinsa.point.domain.enums.PointStatus;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    PointUsageLinkService pointUsageLinkService;
    @Autowired
    PointUsageService pointUsageService;

    @BeforeAll
    public void setup() throws Exception {
        createUser("koo");
    }

    private void createUser(String userId) throws Exception{
        User user = User.of(userId);
        String reqJsonStr = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());
    }

    private Long pointEarn(String userId, Long pointAmount) throws Exception{
        PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of(userId, pointAmount);
        String reqJsonStr = objectMapper.writeValueAsString(pointEarnReqDto);
        MvcResult result = mockMvc.perform(post("/point/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        CommonResponseDto<PointEarnResDto> responseDto = objectMapper.readValue(resultStr, new TypeReference<CommonResponseDto<PointEarnResDto>>(){});
        return responseDto.getResult().pointItemKey();
    }

    private void pointUse(String orderKey, Long pointUseAmount, String userId) throws Exception{
        PointUseReqDto pointUseReqDto = PointUseReqDto.of(orderKey, pointUseAmount, userId);
        String reqJsonStr = objectMapper.writeValueAsString(pointUseReqDto);
        mockMvc.perform(post("/point/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJsonStr)
        ).andExpect(status().isOk());
    }

    private void pointCancel(String orderKey, Long pointCancelAmount, String userId) throws Exception{
        PointUseCancelReqDto pointUseCancelReqDto = PointUseCancelReqDto.of(orderKey, pointCancelAmount, userId);
        String reqJson = objectMapper.writeValueAsString(pointUseCancelReqDto);

        mockMvc.perform(post("/point/use/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson)
        ).andExpect(status().isOk());
    }

    @Nested
    class pointEarn {

        @Transactional
        @Test
        void setPointEarnTest() throws Exception {
            String userId = "koo";
            Long pointAmount = 2000L;

            UserPointInfo beforeUserPointInfo = userPointInfoService.getUserPointInfo(userId);
            PointPolicy policy = pointPolicyService.getUserPolicy(userId);

            long beforeUserPointEarnLimit = policy.getPointEarnLimit();
            long beforeUserPointBalance = beforeUserPointInfo.getPointTotalBalance();

            pointEarn(userId, pointAmount);

            UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(userId);

            log.debug("1회 한도 금액 = {},", beforeUserPointEarnLimit);
            log.debug("이전 잔여 금액 = {}", beforeUserPointBalance);

            //이전 금액과 비교 balance
            Assertions.assertEquals(userPointInfo.getPointTotalBalance(), beforeUserPointBalance + pointAmount);
            List<PointItem> pointItemList = pointItemService.getPointItemList(userId);

            if (CollectionUtils.isEmpty(pointItemList))
                Assertions.fail();

            Assertions.assertEquals(pointItemList.getFirst().getPointAmount(), pointAmount);
        }
    }

    @Nested
    class PointCancel{

        Long pointItemKey;
        Long pointItemKey2;

        @Transactional
        @BeforeEach
        public void setup() throws Exception {
            pointItemKey = pointEarn("koo", 1000L);
            pointItemKey2 = pointEarn("koo", 2000L);
        }

        @Transactional
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
        @Transactional
        @BeforeEach
        public void setup() throws Exception {
            pointEarn("koo", 1000L);
            pointEarn("koo", 2000L);
        }

        @Transactional
        @Test
        public void pointUseTest() throws Exception {
            String orderKey = "test001";
            String userId = "koo";
            Long pointUseAmount = 2000L;

            UserPointInfo beforeUserPointInfo = userPointInfoService.getUserPointInfo(userId);
            log.debug("이전 포인트 잔량 = {}", beforeUserPointInfo.getPointTotalBalance());

            pointUse(orderKey, pointUseAmount, userId);

            UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(userId);
            log.debug("포인트 잔량 = {}", userPointInfo.getPointTotalBalance());

            List<PointUsageLink> pointUsageLinkList = pointUsageLinkService.getPointUsageLinkListByOrderKey(orderKey);
            long usedPointTotal = pointUsageLinkList.stream().mapToLong(PointUsageLink::getPointUsageAmount).sum();
            log.debug("포인트 사용량 = {}", usedPointTotal);

            for(PointUsageLink pointUsageLink : pointUsageLinkList){
                PointItem pointItem = pointUsageLink.getPointItem();
                log.debug("usageLinkId = {}, pointItemKey = {}, pointItemAmount = {}, usageLinkAmount = {}, pointItemStatus = {}"
                        , pointUsageLink.getPointUsageLinkKey(), pointItem.getPointItemKey(), pointItem.getPointAmount(), pointUsageLink.getPointUsageAmount(), pointItem.getPointStatus());
            }
            Assertions.assertEquals(pointUseAmount, usedPointTotal);
        }

    }

    @Nested
    class PointUseCancel{
        @Transactional
        @BeforeEach
        public void setup() throws Exception {
            pointEarn("koo", 1000L);
            pointEarn("koo", 2000L);
            pointUse("test001", 2500L, "koo");
        }

        @Transactional
        @Test
        public void pointUseCancelTest() throws Exception{
            String orderKey = "test001";
            String userId = "koo";
            long pointCancelAmount = 2300;

            pointCancel(orderKey, pointCancelAmount, userId);

            UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(userId);
            log.debug("포인트 잔량 = {}", userPointInfo.getPointTotalBalance());
            Assertions.assertEquals(1000+2000-2500+pointCancelAmount, userPointInfo.getPointTotalBalance());

            List<PointUsage> pointUsageList = pointUsageService.getPointUsageByOrderKey(orderKey);
            for(PointUsage pointUsage : pointUsageList){
                log.debug("pointUsageKey = {}, pointUsageType = {}, pointUsageAmount = {}", pointUsage.getPointUsageKey(), pointUsage.getPointUsageType(), pointUsage.getPointUsageAmount());
            }
            long pointUsageSum = pointUsageList.stream().mapToLong(PointUsage::getPointUsageAmount).sum();
            log.debug("해당 주문에 사용한 포인트 양(header) = {}", pointUsageSum);
            Assertions.assertEquals(200L, pointUsageSum);

            List<PointUsageLink> pointUsageLinkList = pointUsageLinkService.getPointUsageLinkListByOrderKey(orderKey);
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
