package com.dailycodework.buynowdotcom.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private int quantity;
    private BigDecimal price;
    private OrderDTO order;
    private ProductDTO product;
}
