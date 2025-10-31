package com.musinsa.point.domain.entity;

import com.musinsa.point.domain.enums.PointStatus;
import com.musinsa.point.domain.enums.PointType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_item")
public class PointItem extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_item_key")
    private Long pointItemKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_info_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_item_point_info_key"))
    private UserPointInfo userPointInfo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_event_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_item_point_event_key"))
    private PointEvent pointEvent;

    @Column(name = "point_amount")
    private Long pointAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type")
    private PointType pointType;

    @Column(name = "manual_flag")
    private boolean menualFlag = false;

    @Column(name = "point_expiration_dt")
    private LocalDateTime pointExpirationDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_status")
    private PointStatus pointStatus;

    private PointItem(UserPointInfo userPointInfo, PointEvent pointEvent, PointType pointType
                    , Long pointAmount,boolean menualFlag, Long pointExpirationDt, PointStatus pointStatus) {
        this.userPointInfo = userPointInfo;
        this.pointEvent = pointEvent;
        this.pointType = pointType;
        this.pointAmount = pointAmount;
        this.menualFlag = menualFlag;
        this.pointExpirationDt = LocalDateTime.now().plusDays(pointExpirationDt);
        this.pointStatus = pointStatus;
    }

    public static PointItem of(UserPointInfo userPointInfo, PointEvent event, PointType pointType
                            , Long pointAmount, boolean menualFlag, Long pointExpirationDt, PointStatus pointStatus){
        return new PointItem(userPointInfo, event, pointType, pointAmount, menualFlag, pointExpirationDt, pointStatus);
    }

}


