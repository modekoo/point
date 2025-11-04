package com.musinsa.point;

import com.musinsa.point.domain.dto.Point.PointEarnReqDto;
import com.musinsa.point.domain.dto.user.CreateUserReqDto;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.service.PointService;
import com.musinsa.point.domain.service.UserPointInfoService;
import com.musinsa.point.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserPointInfoTests {

    @Autowired
    UserService userService;
    @Autowired
    PointService pointService;
    @Autowired
    UserPointInfoService userPointInfoService;

    String userId;

    @BeforeEach
    public void setup() throws Exception {
        userId = UUID.randomUUID().toString().replaceAll("-", "");
        CreateUserReqDto createUserReqDto = new CreateUserReqDto(userId);
        userService.createUser(createUserReqDto);
    }

	@Test
	void setUserInfoLockTest() throws Exception {

        PointEarnReqDto pointEarnReqDto = PointEarnReqDto.of(userId, 100L);
        var th = Executors.newFixedThreadPool(10);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch cd = new CountDownLatch(10);

        //e clazz = class org.springframework.orm.ObjectOptimisticLockingFailureException
        //e message = Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [com.musinsa.point.domain.entity.UserPointInfo#1]
        for(int i =0; i<10; i++){
            int idx = i;
            th.submit(() -> {
                try {
                    log.debug("thread = {}", idx);
                    start.await();
                    pointService.earnPoint(pointEarnReqDto);
                    log.debug("thread done = {}", idx);
                } catch (Exception e) {
                    log.debug("e clazz = {}", e.getClass());
                    log.debug("e message = {}", e.getMessage());
                } finally {
                    cd.countDown();
                }
            });
        }
        start.countDown();
        cd.await();
        th.shutdown();

        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(userId);
        log.info("잔액 = {}", userPointInfo.getPointTotalBalance());

        Assertions.assertNotEquals(100L * 10, userPointInfo.getPointTotalBalance());
	}
}
