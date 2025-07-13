package com.dailycodework.buynowdotcom.service.cart;

import com.dailycodework.buynowdotcom.dtos.CartDTO;
import com.dailycodework.buynowdotcom.model.Cart;
import com.dailycodework.buynowdotcom.model.User;
import com.dailycodework.buynowdotcom.repository.CartItemRepository;
import com.dailycodework.buynowdotcom.repository.CartRepository;
import com.dailycodework.buynowdotcom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    /**
     * @param cartId existing cart id
     * @return cart
     */
    @Override
    public CartDTO getCartById(Long cartId) {
        return cartRepository.findById(cartId).map(cart -> mapper.map(cart, CartDTO.class)).orElseThrow(() -> new EntityNotFoundException("Cart not found!"));
    }

    /**
     * @param userId existing user id
     * @return cart
     */
    @Override
    public CartDTO getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).map(cart -> mapper.map(cart, CartDTO.class)).orElseThrow(() -> new EntityNotFoundException("Cart not found!"));
    }

    /**
     * @param cartId existing cart id to be removed
     */
    @Override
    public void clearCart(Long cartId) {
        Cart cart = getCartByCartId(cartId);
        cart.clearCartItems();
        cartRepository.save(cart);
        cartItemRepository.deleteAllByCartId(cartId);

    }

    /**
     * @param userId existing user id for which cart to be initialized
     * @return cart
     */
    @Override
    public CartDTO initializeNewCartForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found!"));

        return cartRepository.findByUserId(userId).map(cart -> mapper.map(cart, CartDTO.class)).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setTotalAmount(new BigDecimal(0));
            cart.setUser(user);
            user.setCart(cart);
            userRepository.save(user);
            return getCartByUserId(userId);
        });
    }

    /**
     * @param cartId existing cart id
     * @return total amount of the cart
     */
    @Override
    public BigDecimal getTotalPrice(Long cartId) {
        return getCartById(cartId).getTotalAmount();
    }

    public Cart getCartByCartId(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(()-> new EntityNotFoundException("Cart not found!"));
    }
}