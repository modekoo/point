package com.musinsa.point.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    private Long userNo;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
}
