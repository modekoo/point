package com.musinsa.point.entity;

import com.musinsa.point.enums.OrderStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends DateEntity{
    @Id
    @Column(name = "order_key")
    private String orderKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;
}
