package com.dailycodework.buynowdotcom.repository;

import com.dailycodework.buynowdotcom.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    OrderItem findByProductId(Long productId);
}
