package com.point.domain.service;

import com.point.domain.entity.PointEvent;
import com.point.domain.entity.UserPointInfo;
import com.point.domain.enums.EventType;
import com.point.domain.repository.PointEventRepository;
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
