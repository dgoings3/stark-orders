package com.starknakedpoultry.starkorders;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByIdDesc();

    List<Order> findByStatusOrderByIdDesc(OrderStatus status);

    List<Order> findByStatusInOrderByIdDesc(List<OrderStatus> statuses);
}