package com.dailycodework.buynowdotcom.controller;

import com.dailycodework.buynowdotcom.dtos.OrderDTO;
import com.dailycodework.buynowdotcom.response.ApiResponse;
import com.dailycodework.buynowdotcom.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(value = "http://localhost:5100")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/placed")
    public ResponseEntity<ApiResponse> placeOrder() {
        OrderDTO order = orderService.placeOrder();
        return ResponseEntity
                .ok()
                .body(new ApiResponse(order, false));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getUserOrder() {
        List<OrderDTO> order = orderService.getUserOrder();
        return ResponseEntity
                .ok()
                .body(new ApiResponse(order, false));
    }

}
