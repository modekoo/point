package com.point;

import com.point.consts.PointConst;
import com.point.domain.dto.user.CreateUserReqDto;
import com.point.domain.entity.PointEvent;
import com.point.domain.entity.PointItem;
import com.point.domain.entity.UserPointInfo;
import com.point.domain.enums.EventType;
import com.point.domain.enums.PointType;
import com.point.domain.repository.PointItemRepository;
import com.point.domain.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PointItemTests {

    @Autowired
    PointItemRepository pointItemRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserPointInfoService userPointInfoService;
    @Autowired
    PointEventService pointEventService;
    @Autowired
    PointService pointService;
    @Autowired
    PointItemService pointItemService;

    String userId;

    @BeforeEach
    public void setup() throws Exception {
        userId = UUID.randomUUID().toString().replaceAll("-", "");
        CreateUserReqDto createUserReqDto = new CreateUserReqDto(userId);
        userService.createUser(createUserReqDto);
    }

	@Test
	void getPointValidList() throws Exception {

        long earnAmount = 1000;
        UserPointInfo userPointInfo = userPointInfoService.getUserPointInfo(userId);

        userPointInfo = userPointInfoService.setUserPointBalance(userPointInfo, earnAmount);
        PointEvent pointEvent = pointEventService.createPointEvent(EventType.EARN, userPointInfo);
        PointItem pointItem = pointItemService.createPointItem(userPointInfo, pointEvent, PointType.EARN, earnAmount, false, PointConst.POINT_EXPIRE_DAYS);
        pointItem.setPointExpired();
        pointItemRepository.saveAndFlush(pointItem);

        //만료된 포인트가 조회되면 안됨.
        List<PointItem> pointItemActiveList = pointItemService.getPointItemListByUserIdActive(userId);
        Assertions.assertEquals(new ArrayList<PointItem>(), pointItemActiveList);
	}
}
