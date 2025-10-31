package com.musinsa.point.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    private Long userKey;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
}
