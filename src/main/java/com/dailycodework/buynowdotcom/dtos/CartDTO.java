package com.dailycodework.buynowdotcom.dtos;

import com.dailycodework.buynowdotcom.model.CartItem;
import com.dailycodework.buynowdotcom.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@JsonIgnoreProperties(value = {"user"})
public class CartDTO {

    private Long id;
    private BigDecimal totalAmount;
    private UserDTO user;
    private Set<CartItemDTO> items;
}
