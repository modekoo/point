package com.musinsa.point.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_point_info")
public class UserPointInfo extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_info_key")
    private Long pointInfoKey;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false
                , foreignKey = @ForeignKey(name = "fk_user_point_info_user_key"))
    private User user;

    @Column(name = "point_total_balance")
    private Long pointTotalBalance = 0L;

    @Version
    @Column(name = "version")
    private Long version;

    private UserPointInfo(User user){
        this.user = user;
    }

    public static UserPointInfo of(User user){
        return new UserPointInfo(user);
    }

    public void setTotalBalance(Long amount){
        this.pointTotalBalance = getPointTotalBalance() + amount;
    }
}
