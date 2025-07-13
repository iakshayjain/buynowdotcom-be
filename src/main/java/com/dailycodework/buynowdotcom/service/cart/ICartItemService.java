package com.dailycodework.buynowdotcom.service.cart;

import com.dailycodework.buynowdotcom.dtos.CartItemDTO;

import java.util.Set;

public interface ICartItemService {

    CartItemDTO addItemToCart(Long productId, int quantity);
    CartItemDTO updateItemToCart(Long productId, int quantity);
    void removeItemFromCart(Long productId);
    CartItemDTO getCartItem(Long productId);
    Set<CartItemDTO> getCartItems();

}
