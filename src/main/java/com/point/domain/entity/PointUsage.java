package com.point.domain.entity;

import com.point.domain.enums.PointUsageType;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_key", nullable = false
            , foreignKey = @ForeignKey(name = "fk_point_usage_order_key"))
    private Order order;

    @Column(name = "point_usage_amount")
    private Long pointUsageAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_usage_type")
    private PointUsageType pointUsageType;

    public PointUsage(PointEvent pointEvent, Order order, Long pointUsageAmount, PointUsageType pointUsageType) {
        this.pointEvent = pointEvent;
        this.order = order;
        this.pointUsageAmount = pointUsageAmount;
        this.pointUsageType = pointUsageType;
    }

    public static PointUsage of(PointEvent pointEvent, Order order, Long pointUsageAmount, PointUsageType pointUsageType){
        return new PointUsage(pointEvent, order, pointUsageAmount, pointUsageType);
    }

}
