package com.musinsa.point.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_key")
    private Long orderKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private String orderStatus;
}
