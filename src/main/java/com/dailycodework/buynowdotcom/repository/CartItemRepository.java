package com.dailycodework.buynowdotcom.repository;

import com.dailycodework.buynowdotcom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByProductId(Long productId);

    void deleteAllByCartId(Long cartId);
}
