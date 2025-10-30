package com.musinsa.point.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_point_info")
public class UserPointInfo extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_info_key")
    private Long pointInfoKey;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_key", unique = true, nullable = false
                , foreignKey = @ForeignKey(name = "fk_user_key"))
    private User user;

    @Column(name = "point_reward_max")
    private Long pointRewardMax = 10_000L;

    @Column(name = "point_total_balance")
    private Long pointTotalBalance = 0L;

    @Version
    @Column(name = "version")
    private String version;



}
