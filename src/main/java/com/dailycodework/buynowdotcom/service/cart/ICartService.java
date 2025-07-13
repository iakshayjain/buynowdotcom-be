package com.dailycodework.buynowdotcom.service.cart;

import com.dailycodework.buynowdotcom.dtos.CartDTO;

import java.math.BigDecimal;

public interface ICartService {

    CartDTO getCartById(Long cartId);
    CartDTO getCartByUserId(Long userId);
    void clearCart(Long cartId);
    CartDTO initializeNewCartForUser(Long userId);
    BigDecimal getTotalPrice(Long cartId);

}
