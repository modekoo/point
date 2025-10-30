package com.musinsa.point.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "point_event")
public class PointEvent extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_event_key")
    private Long pointEventKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_info_key", nullable = false
                , foreignKey = @ForeignKey(name = "fk_point_info_key"))
    private UserPointInfo userPointInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private String eventType;
}
