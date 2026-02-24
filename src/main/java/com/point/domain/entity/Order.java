package com.point.domain.entity;

import com.point.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order extends DateEntity{
    @Id
    @Column(name = "order_key")
    private String orderKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    private Order(String orderKey){
        this.orderKey = orderKey;
        orderStatus = OrderStatus.PROCESSING;
    }

    public static Order of(String orderKey){
        return new Order(orderKey);
    }

}
