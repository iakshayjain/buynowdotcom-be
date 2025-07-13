package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.dtos.CartDTO;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cart")
public class CartController {

    private final ICartService cartService;

    @GetMapping("/get/{cartId}")
    public ResponseEntity<ApiResponse> getCartById(@PathVariable Long cartId) {
        CartDTO cart = cartService.getCartById(cartId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cart, false));
    }

    @GetMapping("/get/user/{userId}")
    public ResponseEntity<ApiResponse> getCartByUserId(@PathVariable Long userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cart, false));
    }

    @DeleteMapping("/clear/{cartId}")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse("Cart is cleared", false));
    }

    @PostMapping("/initialize/{userId}")
    public ResponseEntity<ApiResponse> initializeNewCart(@PathVariable Long userId) {
        CartDTO cart = cartService.initializeNewCartForUser(userId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cart, false));
    }

    @GetMapping("/get/total/price/{cartId}")
    public ResponseEntity<ApiResponse> getTotalPrice(@PathVariable Long cartId) {
        BigDecimal totalPrice = cartService.getTotalPrice(cartId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(totalPrice, false));
    }

}
