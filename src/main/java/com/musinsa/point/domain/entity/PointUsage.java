package com.musinsa.point.domain.entity;

import com.musinsa.point.domain.enums.PointUsageStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "point_usage")
public class PointUsage extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_usage_key")
    private Long pointUsageKey;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_event_key", nullable = false
            , foreignKey = @ForeignKey(name = "fk_point_usage_point_event_key"))
    private PointEvent pointEvent;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_key", nullable = false
            , foreignKey = @ForeignKey(name = "fk_point_usage_order_key"))
    private Order order;

    @Column(name = "usage_amount")
    private Long usageAmount;

    @Column(name = "usage_balance")
    private Long usageBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_status")
    private PointUsageStatus pointUsageStatus;

    public PointUsage(PointEvent pointEvent, Order order, Long usageAmount, Long usageBalance, PointUsageStatus pointUsageStatus) {
        this.pointEvent = pointEvent;
        this.order = order;
        this.usageAmount = usageAmount;
        this.usageBalance = usageBalance;
        this.pointUsageStatus = pointUsageStatus;
    }

    public static PointUsage of(PointEvent pointEvent, Order order, Long usageAmount, Long usageBalance, PointUsageStatus pointUsageStatus){
        return new PointUsage(pointEvent, order, usageAmount, usageBalance, pointUsageStatus);
    }

}
