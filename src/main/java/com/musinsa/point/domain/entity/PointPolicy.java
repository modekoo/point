package com.musinsa.point.domain.entity;

import com.musinsa.point.domain.dto.pointPolicy.PointPolicyReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_policy")
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_policy_key")
    Long pointPolicyKey;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_info_key", nullable = false, unique = true
                , foreignKey = @ForeignKey(name = "point_policy_point_info_key_fk"))
    UserPointInfo userPointInfo;

    @Column(name = "point_earn_limit")
    Long pointEarnLimit;

    @Column(name = "point_total_limit")
    Long pointTotalLimit;

    private PointPolicy(UserPointInfo userPointInfo){
        this.userPointInfo = userPointInfo;
        pointEarnLimit = 3000L;
        pointTotalLimit = 10_000L;
    }

    public static PointPolicy of(UserPointInfo userPointInfo){
        return new PointPolicy(userPointInfo);
    }

    public void setPolicyFrom(PointPolicyReqDto pointPolicyReqDto){
        if(pointPolicyReqDto.pointEarnLimit() != null) pointEarnLimit = pointPolicyReqDto.pointEarnLimit();
        if(pointPolicyReqDto.pointTotalLimit() != null) pointTotalLimit = pointPolicyReqDto.pointTotalLimit();
    }

}
