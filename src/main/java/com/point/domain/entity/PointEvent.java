package com.point.domain.entity;

import com.point.domain.enums.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "point_event")
public class PointEvent extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_event_key")
    private Long pointEventKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_info_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_event_point_info_key"))
    private UserPointInfo userPointInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    private PointEvent(UserPointInfo userPointInfo, EventType eventType){
        this.userPointInfo = userPointInfo;
        this.eventType = eventType;
    }

    public static PointEvent of(UserPointInfo userPointInfo, EventType eventType){
        return new PointEvent(userPointInfo, eventType);
    }
}
