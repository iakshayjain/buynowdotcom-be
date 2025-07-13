package com.dailycodework.buynowdotcom.dtos;

import com.dailycodework.buynowdotcom.model.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {

    private Long id;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Product product;
}
