package com.musinsa.point.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_item")
public class PointItem extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_item_key")
    private String pointItemKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_info_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_info_key"))
    private UserPointInfo userPointInfo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_event_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_event_key"))
    private PointEvent pointEvent;

    @Column(name = "point_amount")
    private Long pointAmount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type")
    private String pointType;

    @Column(name = "manual_flag")
    private boolean menualFlag = false;

    @Column(name = "point_expiration_dt")
    private LocalDateTime pointExpirationDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_status")
    private String pointStatus;



}


