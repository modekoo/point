package com.musinsa.point.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "point_usage_link")
public class PointUsageLink extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_usage_link_key")
    private Long pointUsageLinkKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_usage_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_usage_key"))
    private PointUsage pointUsage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_item_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_item_key"))
    private PointItem pointItem;

    @Column(name = "point_usage_amount")
    private Long pointUsageAmount = 0L;

}
