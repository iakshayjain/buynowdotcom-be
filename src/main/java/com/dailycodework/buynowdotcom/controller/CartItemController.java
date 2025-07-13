package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.dtos.CartItemDTO;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.cart.ICartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cart-item")
public class CartItemController {

    private final ICartItemService cartItemService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long productId,
                                                     @RequestParam Integer quantity) {
        CartItemDTO cartItem = cartItemService.addItemToCart(productId, quantity);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cartItem, false));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateItemToCart(@RequestParam Long productId,
                                                        @RequestParam Integer quantity) {
        CartItemDTO cartItem = cartItemService.updateItemToCart(productId, quantity);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cartItem, false));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteItemFromCart(@RequestParam Long productId) {
        cartItemService.removeItemFromCart(productId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse("Item deleted from the cart!", false));
    }

    @GetMapping("/get/product/{productId}")
    public ResponseEntity<ApiResponse> getCartItem(@PathVariable Long productId) {
        CartItemDTO cartItem = cartItemService.getCartItem(productId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cartItem, false));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getAllCartItems() {
        Set<CartItemDTO> cartItem = cartItemService.getCartItems();
        return ResponseEntity
                .ok()
                .body(new ApiResponse(cartItem, false));
    }
}
