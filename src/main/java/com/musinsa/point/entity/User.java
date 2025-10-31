package com.musinsa.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    private Long userKey;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    private User(String userId){
        this.userId = userId;
    }

    public static User of(String userId){
        return new User(userId);
    }
}
