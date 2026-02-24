package com.point.domain.service;

import com.point.domain.entity.Order;
import com.point.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(String orderKey){
        return orderRepository.save(Order.of(orderKey));
    }
}
