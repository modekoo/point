package com.musinsa.point.domain.service;

import com.musinsa.point.domain.entity.PointEvent;
import com.musinsa.point.domain.entity.UserPointInfo;
import com.musinsa.point.domain.enums.EventType;
import com.musinsa.point.domain.repository.PointEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointEventService {
    final PointEventRepository pointEventRepository;

    public PointEvent createPointEvent(EventType eventType, UserPointInfo userPointInfo){
        PointEvent pointEvent= PointEvent.of(userPointInfo, eventType);
        return pointEventRepository.save(pointEvent);
    }
}
