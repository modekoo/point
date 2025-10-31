package com.musinsa.point.entity;

import com.musinsa.point.enums.UsageStatus;
import jakarta.persistence.*;

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
    private Long usageAmount = 0L;

    @Column(name = "usage_balance")
    private Long usage_balance = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_status")
    private UsageStatus usageStatus;
}
