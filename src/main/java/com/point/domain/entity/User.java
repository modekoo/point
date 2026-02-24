package com.point.domain.entity;

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
    @Column(name = "user_id")
    private String userId;

    private User(String userId){
        this.userId = userId;
    }

    public static User of(String userId){
        return new User(userId);
    }
}
