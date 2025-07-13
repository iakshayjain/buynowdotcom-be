package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.dtos.OrderItemDTO;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.order.IOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order/item")
public class OrderItemController {

    private final IOrderItemService orderItemService;

    @GetMapping("/get/order/{orderId}/product/{productId}")
    public ResponseEntity<ApiResponse> getItemByOrderIdAndProductId(@PathVariable Long orderId,
                                                                    @PathVariable Long productId) {
        OrderItemDTO orderItem = orderItemService.getItemByOrderIdAndProductId(orderId, productId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(orderItem, false));
    }

    @GetMapping("/get/order/{orderId}")
    public ResponseEntity<ApiResponse> getAllItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemDTO> orderItem = orderItemService.getAllItemsByOrderId(orderId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(orderItem, false));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToOrder(@RequestParam Long orderId,
                                                      @RequestParam Long productId,
                                                      @RequestParam Integer quantity) {
        OrderItemDTO orderItem = orderItemService.addItemToOrder(orderId, productId, quantity);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(orderItem, false));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateOrderItem(@RequestParam Long orderId,
                                                       @RequestParam Long productId,
                                                       @RequestParam Integer quantity) {
        OrderItemDTO orderItem = orderItemService.updateOrderItem(orderId, productId, quantity);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(orderItem, false));
    }

    @DeleteMapping("/delete/order/{orderId}/product/{productId}")
    public ResponseEntity<ApiResponse> deleteItemFromOrder(@PathVariable Long orderId,
                                                           @PathVariable Long productId) {
        orderItemService.deleteItemFromOrder(orderId, productId);
        return ResponseEntity
                .ok()
                .body(new ApiResponse("Item deleted from order list!", false));
    }
}
